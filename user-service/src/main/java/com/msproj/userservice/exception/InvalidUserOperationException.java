package com.msproj.userservice.exception;

/**
 * Exception thrown for invalid user operations
 */
public class InvalidUserOperationException extends UserServiceException {

    public InvalidUserOperationException(String message) {
        super(message, "INVALID_USER_OPERATION");
    }

    public InvalidUserOperationException(String message, Throwable cause) {
        super(message, "INVALID_USER_OPERATION", cause);
    }
}
