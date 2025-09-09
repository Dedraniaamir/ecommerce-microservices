package com.msproj.orderservice.service;

import com.msproj.orderservice.client.ProductServiceClient;
import com.msproj.orderservice.client.UserServiceClient;
import com.msproj.orderservice.dto.*;
import com.msproj.orderservice.entity.Order;
import com.msproj.orderservice.entity.OrderItem;
import com.msproj.orderservice.entity.OrderStatus;
import com.msproj.orderservice.exception.*;
import com.msproj.orderservice.repository.OrderItemRepository;
import com.msproj.orderservice.repository.OrderRepository;
import com.msproj.orderservice.request.LoyaltyPointsRequest;
import com.msproj.orderservice.request.ProductAvailabilityRequest;
import com.msproj.orderservice.request.StockUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Order Service Implementation with @Transactional and Multithreading
 * <p>
 * Key Features:
 * 1. @Transactional with different propagation levels
 * 2. @Async multithreaded operations
 * 3. Inter-service communication with OpenFeign
 * 4. Circuit breaker patterns
 * 5. Complex business logic with state management
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            UserServiceClient userServiceClient,
                            ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userServiceClient = userServiceClient;
        this.productServiceClient = productServiceClient;
    }

    /**
     * Create Order - Complex transaction with multiple services
     *
     * @Transactional Features:
     * - REQUIRED propagation (joins existing or creates new)
     * - READ_COMMITTED isolation (prevents dirty reads)
     * - 30 second timeout
     * - Rollback on any Exception
     */
    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            timeout = 30,
            rollbackFor = Exception.class
    )
    public OrderResponseDto createOrder(CreateOrderRequestDto requestDto) {
        logger.info("Creating order for customer ID: {}", requestDto.getCustomerId());

        try {
            // Step 1: Validate customer using inter-service communication
            UserDto customer = validateCustomer(requestDto.getCustomerId());

            // Step 2: Validate and reserve inventory
            List<OrderItem> orderItems = validateAndReserveInventory(requestDto.getItems());

            // Step 3: Create order entity with business logic
            Order order = createOrderEntity(requestDto, customer, orderItems);

            // Step 4: Save order (cascades to order items)
            Order savedOrder = orderRepository.save(order);

            // Step 5: Process payment within same transaction
            processPayment(savedOrder, requestDto.getPaymentDetails());

            // Step 6: Confirm order after successful payment
            savedOrder.confirm();
            orderRepository.save(savedOrder);

            logger.info("Order created successfully with ID: {}", savedOrder.getId());

            // ASYNC OPERATIONS: These run in separate threads after transaction commits
            triggerAsyncOrderProcessing(savedOrder.getId());

            return OrderResponseDto.fromEntity(savedOrder);

        } catch (Exception e) {
            logger.error("Failed to create order for customer {}: {}", requestDto.getCustomerId(), e.getMessage());
            // Transaction will rollback automatically due to rollbackFor = Exception.class
            throw new OrderCreationException("Failed to create order: " + e.getMessage(), e);
        }
    }

    /**
     * Update Order Status - REQUIRES_NEW propagation
     *
     * @Transactional Features:
     * - REQUIRES_NEW propagation (always creates new transaction)
     * - Commits independently of parent transaction
     * - Useful for status updates and auditing
     */
    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.READ_COMMITTED
    )
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        logger.info("Updating order {} status to {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Validate state transition using business rules
        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidOrderStateException(
                    String.format("Cannot transition from %s to %s", order.getStatus(), newStatus));
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        // Update additional fields based on status
        switch (newStatus) {
            case SHIPPED -> {
                order.setShippedDate(LocalDateTime.now());
                order.setTrackingNumber(generateTrackingNumber());
            }
            case DELIVERED -> order.setDeliveredDate(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);

        // ASYNC: Send notifications for status changes
        notifyOrderStatusChange(updatedOrder.getId(), oldStatus, newStatus);

        logger.info("Order {} status updated from {} to {}", orderId, oldStatus, newStatus);
        return OrderResponseDto.fromEntity(updatedOrder);
    }

    /**
     * Process Order - Complex transactional workflow
     *
     * @Transactional Features:
     * - REQUIRED propagation
     * - SERIALIZABLE isolation (highest level for critical operations)
     * - 60 second timeout for complex operations
     * - Specific exception rollback rules
     */
    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.SERIALIZABLE,
            timeout = 60,
            rollbackFor = {OrderProcessingException.class, PaymentException.class}
    )
    public void processOrder(Long orderId) {
        logger.info("Processing order: {}", orderId);

        // Use pessimistic locking to prevent concurrent modifications
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStateException("Order must be confirmed before processing");
        }

        try {
            // Step 1: Final inventory check and allocation
            validateInventoryAvailability(order);

            // Step 2: Update product inventories (calls Product Service)
            updateProductInventories(order);

            // Step 3: Add loyalty points (calls User Service)
            addLoyaltyPointsToCustomer(order);

            // Step 4: Update order status to processing
            order.process();

            // Step 5: Save updated order
            orderRepository.save(order);

            logger.info("Order {} processed successfully", orderId);

        } catch (Exception e) {
            logger.error("Failed to process order {}: {}", orderId, e.getMessage());
            // The transaction will rollback automatically
            throw new OrderProcessingException("Order processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get Order by ID - Read-only transaction for performance
     */
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        logger.debug("Fetching order by ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return OrderResponseDto.fromEntity(order);
    }

    /**
     * Get Orders by Customer - Read-only with optimized query
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByCustomer(Long customerId) {
        logger.debug("Fetching orders for customer: {}", customerId);

        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId)
                .stream()
                .map(OrderResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Cancel Order - Demonstrates rollback with compensation
     */
    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public OrderResponseDto cancelOrder(Long orderId, String reason) {
        logger.info("Cancelling order {} with reason: {}", orderId, reason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.isCancelled()) {
            throw new InvalidOrderStateException("Order is already cancelled");
        }

        if (order.isDelivered()) {
            throw new InvalidOrderStateException("Cannot cancel delivered order");
        }

        try {
            // Step 1: Refund payment if already charged
            if (order.getPaymentTransactionId() != null) {
                refundPayment(order);
            }

            // Step 2: Release inventory back to products
            releaseInventory(order);

            // Step 3: Cancel the order
            order.cancel(reason);
            Order cancelledOrder = orderRepository.save(order);

            // ASYNC: Send cancellation notifications
            notifyOrderCancellation(cancelledOrder.getId(), reason);

            logger.info("Order {} cancelled successfully", orderId);
            return OrderResponseDto.fromEntity(cancelledOrder);

        } catch (Exception e) {
            logger.error("Failed to cancel order {}: {}", orderId, e.getMessage());
            throw new OrderCancellationException("Order cancellation failed: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus status) {
        logger.debug("Fetching orders by status: {}", status);

        return orderRepository.findByStatus(status)
                .stream()
                .map(OrderResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryDto> getOrderSummariesByCustomer(Long customerId) {
        logger.debug("Fetching order summaries for customer: {}", customerId);

        return orderRepository.findOrderSummariesByCustomer(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> searchOrders(OrderSearchCriteriaDto criteria) {
        logger.debug("Searching orders with criteria");

        return orderRepository.findOrdersWithFilters(
                        criteria.getCustomerId(),
                        criteria.getStatus(),
                        criteria.getFromDate() != null ? LocalDateTime.parse(criteria.getFromDate()) : null,
                        criteria.getToDate() != null ? LocalDateTime.parse(criteria.getToDate()) : null,
                        criteria.getMinAmount() != null ? new BigDecimal(criteria.getMinAmount()) : null,
                        criteria.getMaxAmount() != null ? new BigDecimal(criteria.getMaxAmount()) : null
                ).stream()
                .map(OrderResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getHighValueOrders(BigDecimal minAmount) {
        logger.debug("Fetching high value orders >= {}", minAmount);

        return orderRepository.findHighValueOrders(minAmount)
                .stream()
                .map(OrderResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOrderAnalytics(String period, Long customerId) {
        logger.debug("Generating order analytics");

        Map<String, Object> analytics = new HashMap<>();

        // Get status breakdown
        List<Object[]> statusStats = orderRepository.getOrderStatsByStatus();
        Map<String, Object> statusAnalytics = new HashMap<>();

        for (Object[] stat : statusStats) {
            OrderStatus status = (OrderStatus) stat[0];
            Long count = (Long) stat[1];
            Double avgAmount = (Double) stat[2];

            statusAnalytics.put(status.name(), Map.of(
                    "count", count,
                    "averageAmount", avgAmount != null ? avgAmount : 0.0
            ));
        }

        analytics.put("statusBreakdown", statusAnalytics);
        analytics.put("timestamp", LocalDateTime.now());

        // Customer-specific stats if requested
        if (customerId != null) {
            Long customerOrderCount = orderRepository.countOrdersByCustomer(customerId);
            BigDecimal totalSpent = orderRepository.calculateTotalSpentByCustomer(customerId);

            analytics.put("customerStats", Map.of(
                    "totalOrders", customerOrderCount,
                    "totalSpent", totalSpent != null ? totalSpent : BigDecimal.ZERO
            ));
        }

        return analytics;
    }

    @Override
    @Transactional
    public Map<String, Object> performBulkOperation(BulkOrderOperationDto operationDto) {
        logger.info("Performing bulk operation: {}", operationDto.getOperation());

        Map<String, Object> result = new HashMap<>();

        switch (operationDto.getOperation().toUpperCase()) {
            case "CANCEL_STALE" -> {
                LocalDateTime cutoffDate = LocalDateTime.now().minusDays(1);
                int cancelledCount = orderRepository.cancelStaleOrders(cutoffDate);
                result.put("cancelledOrders", cancelledCount);
            }

            case "UPDATE_TRACKING" -> {
                List<Long> orderIds = operationDto.getOrderIds();
                int updatedCount = 0;

                if (orderIds != null) {
                    for (Long orderId : orderIds) {
                        String trackingNumber = generateTrackingNumber();
                        int updated = orderRepository.updateTrackingNumber(orderId, trackingNumber);
                        updatedCount += updated;
                    }
                }

                result.put("updatedOrders", updatedCount);
            }

            default -> {
                result.put("error", "Unsupported operation: " + operationDto.getOperation());
            }
        }

        return result;
    }

    // ====== ASYNC METHODS - These run in separate threads ======

    /**
     * Trigger async order processing operations
     * Uses @Async to run in separate thread pool
     */
    @Async("orderProcessingExecutor")
    public void triggerAsyncOrderProcessing(Long orderId) {
        logger.info("Starting async processing for order: {}", orderId);

        try {
            // Simulate some processing time
            Thread.sleep(1000);

            // Send order confirmation email
            sendOrderConfirmationEmail(orderId);

            // Update analytics
            updateOrderAnalytics(orderId);

        } catch (Exception e) {
            logger.error("Async order processing failed for order {}: {}", orderId, e.getMessage());
        }
    }

    /**
     * Send order confirmation email asynchronously
     */
    @Async("notificationExecutor")
    public CompletableFuture<Void> sendOrderConfirmationEmail(Long orderId) {
        logger.info("Sending order confirmation email for order: {}", orderId);

        try {
            // Simulate email sending delay
            Thread.sleep(2000);
            logger.info("Order confirmation email sent for order: {}", orderId);
        } catch (Exception e) {
            logger.error("Failed to send confirmation email for order {}: {}", orderId, e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Update analytics in background
     */
    @Async("analyticsExecutor")
    public void updateOrderAnalytics(Long orderId) {
        logger.debug("Updating analytics for order: {}", orderId);

        try {
            // Simulate analytics processing
            Thread.sleep(5000);
            logger.info("Analytics updated for order: {}", orderId);
        } catch (Exception e) {
            logger.error("Failed to update analytics for order {}: {}", orderId, e.getMessage());
        }
    }

    /**
     * Send status change notifications asynchronously
     */
    @Async("notificationExecutor")
    public void notifyOrderStatusChange(Long orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        logger.info("Sending status change notification for order {}: {} -> {}", orderId, oldStatus, newStatus);

        try {
            // Simulate notification processing
            Thread.sleep(1500);
            logger.info("Status change notification sent for order: {}", orderId);
        } catch (Exception e) {
            logger.error("Failed to send status change notification for order {}: {}", orderId, e.getMessage());
        }
    }

    /**
     * Send cancellation notifications asynchronously
     */
    @Async("notificationExecutor")
    public void notifyOrderCancellation(Long orderId, String reason) {
        logger.info("Sending cancellation notification for order {}, reason: {}", orderId, reason);

        try {
            // Simulate notification sending
            Thread.sleep(2000);
            logger.info("Cancellation notification sent for order: {}", orderId);
        } catch (Exception e) {
            logger.error("Failed to send cancellation notification for order {}: {}", orderId, e.getMessage());
        }
    }

    // ====== MONITORING AND DIAGNOSTICS METHODS ======

    @Override
    public Map<String, Object> getAsyncOperationStatus(Long orderId) {
        Map<String, Object> status = new HashMap<>();
        status.put("orderId", orderId);
        status.put("asyncOperationsEnabled", true);
        status.put("lastChecked", LocalDateTime.now());
        return status;
    }

    @Override
    public void triggerManualProcessing(Long orderId) {
        logger.info("Triggering manual processing for order: {}", orderId);
        triggerAsyncOrderProcessing(orderId);
    }

    @Override
    public Map<String, Object> retryFailedOperations(Long orderId, String operation) {
        logger.info("Retrying operation {} for order {}", operation, orderId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("operation", operation);
        result.put("status", "retried");
        result.put("timestamp", LocalDateTime.now());

        return result;
    }

    @Override
    public Map<String, Object> getServiceCommunicationStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            // Test User Service
            userServiceClient.getUserById(1L);
            status.put("userService", "UP");
        } catch (Exception e) {
            status.put("userService", "DOWN - " + e.getMessage());
        }

        try {
            // Test Product Service
            productServiceClient.getProductById(1L);
            status.put("productService", "UP");
        } catch (Exception e) {
            status.put("productService", "DOWN - " + e.getMessage());
        }

        status.put("timestamp", LocalDateTime.now());
        return status;
    }

    @Override
    public Map<String, Object> testServiceCommunication(Long customerId, Long productId) {
        Map<String, Object> results = new HashMap<>();

        // Test User Service
        try {
            UserDto user = userServiceClient.getUserById(customerId);
            results.put("userServiceTest", Map.of(
                    "status", "SUCCESS",
                    "user", user.username(),
                    "email", user.email()
            ));
        } catch (Exception e) {
            results.put("userServiceTest", Map.of(
                    "status", "FAILED",
                    "error", e.getMessage()
            ));
        }

        // Test Product Service
        try {
            ProductDto product = productServiceClient.getProductById(productId);
            results.put("productServiceTest", Map.of(
                    "status", "SUCCESS",
                    "product", product.name(),
                    "price", product.price(),
                    "available", product.isAvailable()
            ));
        } catch (Exception e) {
            results.put("productServiceTest", Map.of(
                    "status", "FAILED",
                    "error", e.getMessage()
            ));
        }

        results.put("timestamp", LocalDateTime.now());
        return results;
    }

    @Override
    public Map<String, Object> getTransactionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orderRepository.count());
        stats.put("pendingOrders", orderRepository.findByStatus(OrderStatus.PENDING).size());
        stats.put("completedOrders", orderRepository.findByStatus(OrderStatus.DELIVERED).size());
        stats.put("timestamp", LocalDateTime.now());
        return stats;
    }

    // ====== PRIVATE HELPER METHODS ======

    /**
     * Validate customer using inter-service communication
     */
    private UserDto validateCustomer(Long customerId) {
        try {
            // Using OpenFeign client with circuit breaker
            UserDto customer = userServiceClient.getUserById(customerId);

            if (customer == null) {
                throw new CustomerNotFoundException(customerId);
            }

            return customer;
        } catch (Exception e) {
            logger.error("Failed to validate customer {}: {}", customerId, e.getMessage());
            throw new CustomerValidationException("Customer validation failed", e);
        }
    }

    /**
     * Validate and reserve inventory for order items
     */
    private List<OrderItem> validateAndReserveInventory(List<CreateOrderItemDto> itemDtos) {
        List<OrderItem> orderItems = new ArrayList<>();

        // Get all product IDs for batch validation
        List<Long> productIds = itemDtos.stream()
                .map(CreateOrderItemDto::getProductId)
                .collect(Collectors.toList());

        // Batch call to Product Service for efficiency
        List<ProductDto> products = productServiceClient.getProductsByIds(productIds);

        // Create map for quick lookup
        Map<Long, ProductDto> productMap = products.stream()
                .collect(Collectors.toMap(ProductDto::id, p -> p));

        for (CreateOrderItemDto itemDto : itemDtos) {
            ProductDto product = productMap.get(itemDto.getProductId());

            if (product == null) {
                throw new ProductNotFoundException(itemDto.getProductId());
            }

            if (!product.isAvailable()) {
                throw new ProductNotAvailableException(product.name());
            }

            if (product.stockQuantity() < itemDto.getQuantity()) {
                throw new InsufficientStockException(
                        product.name(), product.stockQuantity(), itemDto.getQuantity());
            }

            // Create order item
            OrderItem orderItem = new OrderItem(
                    product.id(),
                    product.name(),
                    product.sku(),
                    product.price(),
                    itemDto.getQuantity()
            );

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    /**
     * Create order entity with all required information
     */
    private Order createOrderEntity(CreateOrderRequestDto requestDto, UserDto customer, List<OrderItem> orderItems) {
        Order order = new Order(customer.id(), customer.fullName(), customer.email());

        // Add order items
        for (OrderItem item : orderItems) {
            order.addOrderItem(item);
        }

        // Set addresses
        if (requestDto.getShippingAddress() != null) {
            order.setShippingAddress(requestDto.getShippingAddress());
        }

        if (requestDto.getBillingAddress() != null) {
            order.setBillingAddress(requestDto.getBillingAddress());
        }

        // Set payment method
        order.setPaymentMethod(requestDto.getPaymentMethod());

        // Calculate shipping and tax
        order.setShippingAmount(calculateShippingAmount(order));
        order.setTaxAmount(calculateTaxAmount(order));

        // Apply customer discount if applicable
        if ("GOLD".equals(customer.customerTier()) || "PLATINUM".equals(customer.customerTier())) {
            BigDecimal discountPercent = "PLATINUM".equals(customer.customerTier()) ?
                    BigDecimal.valueOf(0.15) : BigDecimal.valueOf(0.10);
            BigDecimal discount = order.getTotalAmount().multiply(discountPercent);
            order.setDiscountAmount(discount);
        }

        // Recalculate final totals
        order.recalculateTotals();

        return order;
    }

    /**
     * Process payment within the transaction
     */
    private void processPayment(Order order, PaymentDetailsDto paymentDetails) {
        try {
            // Simulate payment processing
            Thread.sleep(500); // Simulate payment gateway delay

            // Generate transaction ID
            String transactionId = "TXN_" + System.currentTimeMillis() + "_" +
                    (int) (Math.random() * 9999);

            order.setPaymentTransactionId(transactionId);

            logger.info("Payment processed successfully for order {}. Transaction: {}",
                    order.getId(), transactionId);

        } catch (Exception e) {
            logger.error("Payment failed for order {}: {}", order.getId(), e.getMessage());
            throw new PaymentException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Update product inventories after order processing
     */
    private void updateProductInventories(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            try {
                StockUpdateRequest stockUpdate = new StockUpdateRequest(-item.getQuantity(), "REDUCE");
                productServiceClient.updateProductStock(item.getProductId(), stockUpdate);

                logger.debug("Updated inventory for product {}: reduced by {}",
                        item.getProductId(), item.getQuantity());

            } catch (Exception e) {
                logger.error("Failed to update stock for product {}: {}", item.getProductId(), e.getMessage());
                throw new InventoryUpdateException("Failed to update product inventory", e);
            }
        }
    }

    /**
     * Add loyalty points to customer
     */
    private void addLoyaltyPointsToCustomer(Order order) {
        try {
            // Calculate loyalty points (1 point per dollar spent)
            int loyaltyPoints = order.getFinalAmount().intValue();

            LoyaltyPointsRequest request = new LoyaltyPointsRequest(loyaltyPoints);
            userServiceClient.addLoyaltyPoints(order.getCustomerId(), request);

            logger.info("Added {} loyalty points to customer {}", loyaltyPoints, order.getCustomerId());

        } catch (Exception e) {
            logger.warn("Failed to add loyalty points for customer {}: {}",
                    order.getCustomerId(), e.getMessage());
            // Don't fail the order for loyalty points failure - this is non-critical
        }
    }

    private void validateInventoryAvailability(Order order) {
        List<ProductAvailabilityRequest> requests = order.getOrderItems().stream()
                .map(item -> new ProductAvailabilityRequest(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        Map<Long, Boolean> availability = productServiceClient.checkProductsAvailability(requests);

        for (OrderItem item : order.getOrderItems()) {
            if (!availability.getOrDefault(item.getProductId(), false)) {
                throw new InsufficientStockException(
                        item.getProductName(), 0, item.getQuantity());
            }
        }
    }

    private void releaseInventory(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            try {
                StockUpdateRequest stockUpdate = new StockUpdateRequest(item.getQuantity(), "ADD");
                productServiceClient.updateProductStock(item.getProductId(), stockUpdate);

                logger.debug("Released inventory for product {}: added back {}",
                        item.getProductId(), item.getQuantity());

            } catch (Exception e) {
                logger.error("Failed to release inventory for product {}: {}",
                        item.getProductId(), e.getMessage());
                // Continue with other items even if one fails
            }
        }
    }

    private void refundPayment(Order order) {
        try {
            // Simulate refund processing
            Thread.sleep(1000);

            logger.info("Processing refund for order {} with transaction {}",
                    order.getId(), order.getPaymentTransactionId());

            // In real implementation, call payment gateway refund API

        } catch (Exception e) {
            logger.error("Failed to refund payment for order {}: {}", order.getId(), e.getMessage());
            throw new PaymentRefundException("Payment refund failed", e);
        }
    }

    private BigDecimal calculateShippingAmount(Order order) {
        BigDecimal baseShipping = BigDecimal.valueOf(9.99);

        // Free shipping for orders over $100
        if (order.getTotalAmount().compareTo(BigDecimal.valueOf(100)) >= 0) {
            return BigDecimal.ZERO;
        }

        return baseShipping;
    }

    private BigDecimal calculateTaxAmount(Order order) {
        // Simple tax calculation - 8.5% tax rate
        BigDecimal taxRate = BigDecimal.valueOf(0.085);
        return order.getTotalAmount().multiply(taxRate);
    }

    private String generateTrackingNumber() {
        return "TRK" + System.currentTimeMillis() +
                String.format("%04d", new Random().nextInt(10000));
    }
}


