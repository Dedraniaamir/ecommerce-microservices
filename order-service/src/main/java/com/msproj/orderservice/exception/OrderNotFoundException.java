package com.msproj.orderservice.exception;

public class OrderNotFoundException extends OrderServiceException {
    public OrderNotFoundException(String message) {
        super(message, "ORDER_NOT_FOUND");
    }

    public OrderNotFoundException(Long orderId) {
        super("Order not found with ID: " + orderId, "ORDER_NOT_FOUND");
    }
}
