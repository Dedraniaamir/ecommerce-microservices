package com.msproj.orderservice.exception;

public class ProductNotFoundException extends OrderServiceException {
    public ProductNotFoundException(Long productId) {
        super("Product not found with ID: " + productId, "PRODUCT_NOT_FOUND");
    }
}
