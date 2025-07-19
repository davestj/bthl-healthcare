package com.bthl.healthcare.model.enums;

public enum UserType {
    ADMIN("System Administrator", "Full system access and user management capabilities"),
    BROKER("Insurance Broker", "Licensed professional managing client healthcare benefit selections"),
    PROVIDER("Insurance Provider", "Insurance company representative managing plan offerings and broker relationships"),
    COMPANY_USER("Company Representative", "Company HR or benefits administrator managing employee healthcare coverage");

    private final String displayName;
    private final String description;

    UserType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasAdminPrivileges() {
        return this == ADMIN;
    }

    public boolean worksWithMultipleOrganizations() {
        return this == BROKER || this == PROVIDER;
    }

    public boolean isExternalStakeholder() {
        return this == BROKER || this == PROVIDER || this == COMPANY_USER;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
