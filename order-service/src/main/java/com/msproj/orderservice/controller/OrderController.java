package com.msproj.orderservice.controller;

import com.msproj.orderservice.dto.*;
import com.msproj.orderservice.entity.OrderStatus;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Order Controller with REST APIs
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create a new order
     */
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody CreateOrderRequestDto requestDto) {
        logger.info("POST /api/orders - Creating order for customer: {}", requestDto.getCustomerId());

        OrderResponseDto response = orderService.createOrder(requestDto);

        logger.info("Order created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        logger.debug("GET /api/orders/{} - Fetching order", id);

        OrderResponseDto response = orderService.getOrderById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Get orders by customer ID
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByCustomer(@PathVariable Long customerId) {
        logger.debug("GET /api/orders/customer/{} - Fetching customer orders", customerId);

        List<OrderResponseDto> orders = orderService.getOrdersByCustomer(customerId);

        return ResponseEntity.ok(orders);
    }

    /**
     * Update order status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderStatusDto requestDto) {

        logger.info("PATCH /api/orders/{}/status - Updating status to: {}", id, requestDto.getNewStatus());

        OrderResponseDto response = orderService.updateOrderStatus(id, requestDto.getNewStatus());

        return ResponseEntity.ok(response);
    }

    /**
     * Process order
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<Void> processOrder(@PathVariable Long id) {
        logger.info("POST /api/orders/{}/process - Processing order", id);

        orderService.processOrder(id);

        logger.info("Order {} processing initiated", id);
        return ResponseEntity.ok().build();
    }

    /**
     * Cancel order
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable Long id,
            @RequestBody @Valid CancelOrderRequestDto requestDto) {

        logger.info("DELETE /api/orders/{} - Cancelling order, reason: {}", id, requestDto.getReason());

        OrderResponseDto response = orderService.cancelOrder(id, requestDto.getReason());

        return ResponseEntity.ok(response);
    }

    /**
     * Get order summary by customer
     */
    @GetMapping("/customer/{customerId}/summary")
    public ResponseEntity<List<OrderSummaryDto>> getOrderSummariesByCustomer(@PathVariable Long customerId) {
        logger.debug("GET /api/orders/customer/{}/summary - Fetching order summaries", customerId);

        List<OrderSummaryDto> summaries = orderService.getOrderSummariesByCustomer(customerId);

        return ResponseEntity.ok(summaries);
    }

    /**
     * Search orders with filters
     */
    @PostMapping("/search")
    public ResponseEntity<List<OrderResponseDto>> searchOrders(@RequestBody OrderSearchCriteriaDto criteria) {
        logger.debug("POST /api/orders/search - Searching orders with criteria");

        List<OrderResponseDto> orders = orderService.searchOrders(criteria);

        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        logger.debug("GET /api/orders/status/{} - Fetching orders by status", status);

        List<OrderResponseDto> orders = orderService.getOrdersByStatus(status);

        return ResponseEntity.ok(orders);
    }

    /**
     * Get order analytics
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getOrderAnalytics(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Long customerId) {

        logger.debug("GET /api/orders/analytics - Generating analytics, period: {}, customer: {}", period, customerId);

        Map<String, Object> analytics = orderService.getOrderAnalytics(period, customerId);

        return ResponseEntity.ok(analytics);
    }

    /**
     * Get high-value orders
     */
    @GetMapping("/high-value")
    public ResponseEntity<List<OrderResponseDto>> getHighValueOrders(
            @RequestParam(defaultValue = "1000") String minAmount) {

        logger.debug("GET /api/orders/high-value - Fetching orders >= ${}", minAmount);

        List<OrderResponseDto> orders = orderService.getHighValueOrders(new BigDecimal(minAmount));

        return ResponseEntity.ok(orders);
    }

    /**
     * Bulk order operations
     */
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> bulkOrderOperations(@RequestBody BulkOrderOperationDto operationDto) {
        logger.info("POST /api/orders/bulk - Performing bulk operation: {}", operationDto.getOperation());

        Map<String, Object> result = orderService.performBulkOperation(operationDto);

        return ResponseEntity.ok(result);
    }

    /**
     * Get async operation status
     */
    @GetMapping("/{id}/async-status")
    public ResponseEntity<Map<String, Object>> getAsyncOperationStatus(@PathVariable Long id) {
        logger.debug("GET /api/orders/{}/async-status - Checking async operation status", id);

        Map<String, Object> status = orderService.getAsyncOperationStatus(id);

        return ResponseEntity.ok(status);
    }

    /**
     * Trigger manual order processing
     */
    @PostMapping("/{id}/manual-process")
    public ResponseEntity<Void> triggerManualProcessing(@PathVariable Long id) {
        logger.info("POST /api/orders/{}/manual-process - Triggering manual processing", id);

        orderService.triggerManualProcessing(id);

        return ResponseEntity.accepted().build();
    }

    /**
     * Retry failed operations
     */
    @PostMapping("/{id}/retry")
    public ResponseEntity<Map<String, Object>> retryFailedOperations(
            @PathVariable Long id,
            @RequestParam(required = false) String operation) {

        logger.info("POST /api/orders/{}/retry - Retrying operations: {}", id, operation);

        Map<String, Object> result = orderService.retryFailedOperations(id, operation);

        return ResponseEntity.ok(result);
    }

    /**
     * Get service communication status
     */
    @GetMapping("/monitoring/service-status")
    public ResponseEntity<Map<String, Object>> getServiceCommunicationStatus() {
        logger.debug("GET /api/orders/monitoring/service-status - Checking service communication");

        Map<String, Object> status = orderService.getServiceCommunicationStatus();

        return ResponseEntity.ok(status);
    }

    /**
     * Test inter-service communication
     */
    @GetMapping("/test/communication")
    public ResponseEntity<Map<String, Object>> testServiceCommunication(
            @RequestParam(required = false, defaultValue = "1") Long customerId,
            @RequestParam(required = false, defaultValue = "1") Long productId) {

        logger.debug("GET /api/orders/test/communication - Testing service communication");

        Map<String, Object> results = orderService.testServiceCommunication(customerId, productId);

        return ResponseEntity.ok(results);
    }

    /**
     * Get transaction statistics
     */
    @GetMapping("/monitoring/transactions")
    public ResponseEntity<Map<String, Object>> getTransactionStatistics() {
        logger.debug("GET /api/orders/monitoring/transactions - Fetching transaction statistics");

        Map<String, Object> stats = orderService.getTransactionStatistics();

        return ResponseEntity.ok(stats);
    }
}