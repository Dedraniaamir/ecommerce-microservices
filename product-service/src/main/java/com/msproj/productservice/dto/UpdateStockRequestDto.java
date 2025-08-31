package com.msproj.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; /**
 * Update Stock Request DTO
 */
public class UpdateStockRequestDto {
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @NotBlank(message = "Operation type is required")
    private String operation; // "ADD" or "REDUCE"

    public UpdateStockRequestDto() {}

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
}
