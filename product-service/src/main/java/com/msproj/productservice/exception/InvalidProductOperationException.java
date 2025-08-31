package com.msproj.productservice.exception;

public class InvalidProductOperationException extends ProductServiceException {
    public InvalidProductOperationException(String message) {
        super(message, "INVALID_PRODUCT_OPERATION");
    }
}
