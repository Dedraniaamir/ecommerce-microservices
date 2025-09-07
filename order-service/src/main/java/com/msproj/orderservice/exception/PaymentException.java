package com.msproj.orderservice.exception;

public class PaymentException extends OrderServiceException {
    public PaymentException(String message) {
        super(message, "PAYMENT_FAILED");
    }

    public PaymentException(String message, Throwable cause) {
        super(message, "PAYMENT_FAILED", cause);
    }
}
