package com.bthl.healthcare.model.enums;

public enum UserStatus {
    PENDING("Pending verification and approval"),
    ACTIVE("Active user with full access"),
    INACTIVE("Temporarily disabled account"),
    SUSPENDED("Account suspended due to violations"),
    LOCKED("Account locked for security reasons"),
    EXPIRED("Account expired and requires renewal");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean allowsLogin() {
        return this == ACTIVE;
    }

    public boolean requiresAdminAttention() {
        return this == SUSPENDED || this == LOCKED || this == EXPIRED;
    }

    @Override
    public String toString() {
        return description;
    }
}
