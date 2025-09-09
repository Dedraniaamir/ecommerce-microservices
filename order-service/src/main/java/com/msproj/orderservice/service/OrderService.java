package com.msproj.orderservice.service;

import com.msproj.orderservice.dto.*;
import com.msproj.orderservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map; /**
 * Order Service Interface
 */
public interface OrderService {
    // Core order operations
    OrderResponseDto createOrder(CreateOrderRequestDto requestDto);

    OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus);

    void processOrder(Long orderId);

    OrderResponseDto cancelOrder(Long orderId, String reason);

    // Query operations
    OrderResponseDto getOrderById(Long orderId);

    List<OrderResponseDto> getOrdersByCustomer(Long customerId);

    List<OrderResponseDto> getOrdersByStatus(OrderStatus status);

    List<OrderSummaryDto> getOrderSummariesByCustomer(Long customerId);

    List<OrderResponseDto> searchOrders(OrderSearchCriteriaDto criteria);

    List<OrderResponseDto> getHighValueOrders(BigDecimal minAmount);

    // Analytics and reporting
    Map<String, Object> getOrderAnalytics(String period, Long customerId);
    Map<String, Object> performBulkOperation(BulkOrderOperationDto operationDto);

    // Monitoring methods
    Map<String, Object> getAsyncOperationStatus(Long orderId);
    void triggerManualProcessing(Long orderId);
    Map<String, Object> retryFailedOperations(Long orderId, String operation);
    Map<String, Object> getServiceCommunicationStatus();
    Map<String, Object> testServiceCommunication(Long customerId, Long productId);
    Map<String, Object> getTransactionStatistics();

}
