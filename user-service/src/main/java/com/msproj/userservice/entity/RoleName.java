package com.msproj.userservice.entity;

/**
 * Role Name Enum demonstrating proper enum design
 */
public enum RoleName {
    ADMIN("Administrator"),
    CUSTOMER("Customer"),
    MODERATOR("Moderator"),
    SUPPORT("Support Agent");

    private final String displayName;

    RoleName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Business method in enum
    public boolean hasAdminPrivileges() {
        return this == ADMIN || this == MODERATOR;
    }
}
