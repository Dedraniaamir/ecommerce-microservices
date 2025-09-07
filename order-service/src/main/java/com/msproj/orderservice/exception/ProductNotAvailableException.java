package com.msproj.orderservice.exception;

public class ProductNotAvailableException extends OrderServiceException {
    public ProductNotAvailableException(String productName) {
        super("Product is not available: " + productName, "PRODUCT_NOT_AVAILABLE");
    }
}
