package com.msproj.userservice.dto;

import com.msproj.userservice.entity.UserStatus;

/**
 * Additional DTOs for specific operations
 */
public class UpdateStatusRequestDto {
    private UserStatus status;

    public UpdateStatusRequestDto() {
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
