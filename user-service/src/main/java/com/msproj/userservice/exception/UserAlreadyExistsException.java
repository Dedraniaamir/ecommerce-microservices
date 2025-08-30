package com.msproj.userservice.exception;

/**
 * Exception thrown when trying to create a user that already exists
 */
public class UserAlreadyExistsException extends UserServiceException {

    public UserAlreadyExistsException(String message) {
        super(message, "USER_ALREADY_EXISTS");
    }

    public UserAlreadyExistsException(String field, String value) {
        super("User already exists with " + field + ": " + value, "USER_ALREADY_EXISTS");
    }
}
