package com.msproj.userservice.exception;

/**
 * Exception thrown for role-related operations
 */
public class RoleException extends UserServiceException {

    public RoleException(String message) {
        super(message, "ROLE_ERROR");
    }

    public RoleException(String message, Throwable cause) {
        super(message, "ROLE_ERROR", cause);
    }
}
