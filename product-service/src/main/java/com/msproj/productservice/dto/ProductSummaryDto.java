package com.msproj.productservice.dto;

import com.msproj.productservice.entity.ProductStatus;

import java.math.BigDecimal;

/**
 * Product DTOs demonstrating Java 8 features and Collections
 */

// RECORD for immutable data transfer
public record ProductSummaryDto(
        Long id,
        String name,
        BigDecimal price,
        Integer stockQuantity,
        ProductStatus status
) {
    // RECORD with custom methods
    public boolean isAvailable() {
        return ProductStatus.ACTIVE.equals(status) && stockQuantity > 0;
    }

    public String getFormattedPrice() {
        return "$" + price.toString();
    }
}

