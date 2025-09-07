package com.msproj.orderservice.exception;

/**
 * Order Service Custom Exceptions
 */
public class OrderServiceException extends RuntimeException {
    private final String errorCode;

    public OrderServiceException(String message) {
        super(message);
        this.errorCode = "ORDER_SERVICE_ERROR";
    }

    public OrderServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public OrderServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "ORDER_SERVICE_ERROR";
    }

    public OrderServiceException(String message, String orderCancellationFailed, Throwable cause, String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

