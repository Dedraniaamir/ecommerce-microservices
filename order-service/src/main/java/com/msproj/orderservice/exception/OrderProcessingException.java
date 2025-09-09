package com.msproj.orderservice.exception;

public class OrderProcessingException extends OrderServiceException {
    public OrderProcessingException(String message) {
        super(message, "ORDER_PROCESSING_FAILED");
    }

    public OrderProcessingException(String message, Throwable cause) {
        super(message, "ORDER_PROCESSING_FAILED", cause, "005");
    }
}
