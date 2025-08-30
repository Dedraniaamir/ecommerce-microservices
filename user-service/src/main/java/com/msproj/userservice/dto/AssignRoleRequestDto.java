package com.msproj.userservice.dto;

import com.msproj.userservice.entity.RoleName;

public class AssignRoleRequestDto {
    private RoleName roleName;

    public AssignRoleRequestDto() {
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }
}
