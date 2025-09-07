package com.msproj.orderservice.dto;

import com.msproj.orderservice.entity.OrderStatus;
import jakarta.validation.constraints.NotNull; /**
 * Update Order Status DTO
 */
public class UpdateOrderStatusDto {
    @NotNull(message = "New status is required")
    private OrderStatus newStatus;

    private String reason;

    // Constructors
    public UpdateOrderStatusDto() {}

    public UpdateOrderStatusDto(OrderStatus newStatus, String reason) {
        this.newStatus = newStatus;
        this.reason = reason;
    }

    // Getters and Setters
    public OrderStatus getNewStatus() { return newStatus; }
    public void setNewStatus(OrderStatus newStatus) { this.newStatus = newStatus; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
