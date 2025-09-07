package com.msproj.orderservice.exception;

public class OrderCreationException extends OrderServiceException {
    public OrderCreationException(String message) {
        super(message, "ORDER_CREATION_FAILED");
    }

    public OrderCreationException(String message, Throwable cause) {
        super(message, "ORDER_CREATION_FAILED", cause);
    }
}
