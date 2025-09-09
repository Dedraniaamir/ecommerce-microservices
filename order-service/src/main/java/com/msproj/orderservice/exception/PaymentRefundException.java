package com.msproj.orderservice.exception;

public class PaymentRefundException extends OrderServiceException {
    public PaymentRefundException(String message) {
        super(message, "PAYMENT_REFUND_FAILED");
    }

    public PaymentRefundException(String message, Throwable cause) {
        super(message, "PAYMENT_REFUND_FAILED", cause, "007");
    }
}
