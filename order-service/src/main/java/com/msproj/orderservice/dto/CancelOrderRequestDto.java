package com.msproj.orderservice.dto;

import jakarta.validation.constraints.NotBlank; /**
 * Cancel Order Request DTO
 */
public class CancelOrderRequestDto {
    @NotBlank(message = "Cancellation reason is required")
    private String reason;

    // Constructors
    public CancelOrderRequestDto() {}

    public CancelOrderRequestDto(String reason) {
        this.reason = reason;
    }

    // Getters and Setters
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
