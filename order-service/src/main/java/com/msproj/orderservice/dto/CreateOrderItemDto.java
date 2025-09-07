package com.msproj.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull; /**
 * Create Order Item DTO
 */
public class CreateOrderItemDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // Constructors
    public CreateOrderItemDto() {}

    public CreateOrderItemDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
