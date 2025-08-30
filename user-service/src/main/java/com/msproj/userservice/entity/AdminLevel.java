package com.msproj.userservice.entity;

/**
 * Admin Level Enum
 */
public enum AdminLevel {
    JUNIOR("Junior Admin"),
    SENIOR("Senior Admin"),
    SUPER_ADMIN("Super Administrator");

    private final String displayName;

    AdminLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
