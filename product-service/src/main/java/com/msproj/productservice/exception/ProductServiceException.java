package com.msproj.productservice.exception;

/**
 * Product Service Custom Exceptions
 *
 * Exception Hierarchy:
 * 1. ProductServiceException (base)
 * 2. ProductNotFoundException
 * 3. CategoryNotFoundException
 * 4. InsufficientStockException
 * 5. InvalidProductOperationException
 */

public class ProductServiceException extends RuntimeException {
    private final String errorCode;

    public ProductServiceException(String message) {
        super(message);
        this.errorCode = "PRODUCT_SERVICE_ERROR";
    }

    public ProductServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ProductServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PRODUCT_SERVICE_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}

