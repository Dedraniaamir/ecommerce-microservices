package com.msproj.orderservice.exception;

public class InvalidOrderStateException extends OrderServiceException {
    public InvalidOrderStateException(String message) {
        super(message, "INVALID_ORDER_STATE");
    }
}
