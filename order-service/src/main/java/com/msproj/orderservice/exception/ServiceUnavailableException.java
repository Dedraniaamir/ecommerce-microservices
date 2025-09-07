package com.msproj.orderservice.exception;

/**
 * Custom exceptions for service communication
 */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
