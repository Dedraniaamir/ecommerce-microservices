package com.msproj.orderservice.exception;

public class InsufficientStockException extends OrderServiceException {
    public InsufficientStockException(String productName, Integer available, Integer requested) {
        super(String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                productName, available, requested), "INSUFFICIENT_STOCK");
    }
}
