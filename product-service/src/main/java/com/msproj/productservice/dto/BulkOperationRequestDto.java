package com.msproj.productservice.dto;

import java.math.BigDecimal;
import java.util.List; /**
 * Bulk Operation Request DTO
 */
public class BulkOperationRequestDto {
    private String operation;
    private List<CreateProductRequestDto> products;
    private List<Long> productIds;
    private BigDecimal priceMultiplier;

    // Getters and Setters
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public List<CreateProductRequestDto> getProducts() { return products; }
    public void setProducts(List<CreateProductRequestDto> products) { this.products = products; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public BigDecimal getPriceMultiplier() { return priceMultiplier; }
    public void setPriceMultiplier(BigDecimal priceMultiplier) { this.priceMultiplier = priceMultiplier; }
}
