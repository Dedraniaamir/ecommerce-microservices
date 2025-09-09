package com.msproj.orderservice.exception;

public class OrderCancellationException extends OrderServiceException {
    public OrderCancellationException(String message) {
        super(message, "ORDER_CANCELLATION_FAILED");
    }

    public OrderCancellationException(String message, Throwable cause) {
        super(message, "ORDER_CANCELLATION_FAILED", cause, "003");
    }
}
