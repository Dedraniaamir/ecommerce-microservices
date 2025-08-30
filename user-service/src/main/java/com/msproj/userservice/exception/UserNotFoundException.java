package com.msproj.userservice.exception;

/**
 * Exception thrown when user is not found
 */
public class UserNotFoundException extends UserServiceException {

    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND");
    }

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId, "USER_NOT_FOUND");
    }

    public UserNotFoundException(String field, String value) {
        super("User not found with " + field + ": " + value, "USER_NOT_FOUND");
    }
}
