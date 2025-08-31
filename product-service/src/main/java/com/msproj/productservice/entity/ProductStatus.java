package com.msproj.productservice.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors; /**
 * Product Status Enum
 */
public enum ProductStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    OUT_OF_STOCK("Out of Stock"),
    DISCONTINUED("Discontinued"),
    DRAFT("Draft");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // JAVA 8: Static method in enum
    public static List<ProductStatus> getActiveStatuses() {
        return Arrays.stream(values())
                .filter(status -> status == ACTIVE || status == OUT_OF_STOCK)
                .collect(Collectors.toList());
    }
}
