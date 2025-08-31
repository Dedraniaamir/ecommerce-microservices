package com.msproj.productservice.exception;

public class InsufficientStockException extends ProductServiceException {
    public InsufficientStockException(String message) {
        super(message, "INSUFFICIENT_STOCK");
    }

    public InsufficientStockException(String productName, Integer available, Integer requested) {
        super(String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                productName, available, requested), "INSUFFICIENT_STOCK");
    }
}
