package com.msproj.userservice.exception;
/**
 * Custom Exceptions demonstrating Exception Handling best practices
 *
 * Exception Handling Concepts:
 * 1. Custom exceptions for business logic
 * 2. Meaningful error messages
 * 3. Exception hierarchy
 * 4. Runtime vs Checked exceptions
 */

/**
 * Base exception for User Service
 */
public class UserServiceException extends RuntimeException {

    private final String errorCode;

    public UserServiceException(String message) {
        super(message);
        this.errorCode = "USER_SERVICE_ERROR";
    }

    public UserServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "USER_SERVICE_ERROR";
    }

    public UserServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

