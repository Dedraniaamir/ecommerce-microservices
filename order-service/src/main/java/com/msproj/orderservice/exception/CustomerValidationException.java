package com.msproj.orderservice.exception;

public class CustomerValidationException extends OrderServiceException {
    public CustomerValidationException(String message, Throwable cause) {
        super(message, "CUSTOMER_VALIDATION_FAILED", cause);
    }
}
