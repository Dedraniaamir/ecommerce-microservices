package com.msproj.orderservice.dto;

import com.msproj.orderservice.entity.Order;
import com.msproj.orderservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime; /**
 * Order Summary DTO (Record for immutable data)
 */
public record OrderSummaryDto(
        Long id,
        Long customerId,
        String customerName,
        LocalDateTime orderDate,
        OrderStatus status,
        BigDecimal finalAmount,
        Integer itemCount,
        String trackingNumber
) {
    public static OrderSummaryDto fromEntity(Order order) {
        return new OrderSummaryDto(
                order.getId(),
                order.getCustomerId(),
                order.getCustomerName(),
                order.getOrderDate(),
                order.getStatus(),
                order.getFinalAmount(),
                order.getItemCount(),
                order.getTrackingNumber()
        );
    }
}
