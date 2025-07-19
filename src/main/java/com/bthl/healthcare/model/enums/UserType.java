/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/enums/UserType.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: User type enumeration for role-based access control and user categorization
 * Description: I designed this enumeration to categorize users based on their primary function
 *              within my healthcare platform, enabling appropriate feature access and workflows.
 * 
 * Changelog:
 * 2025-07-18: Separated from UserStatus.java file to resolve Java compilation errors
 * 2025-07-16: Initial creation of UserType enum with healthcare industry role definitions
 * 
 * Git Commit: git commit -m "refactor: separate UserType enum into individual file to resolve compilation errors"
 * 
 * Next Dev Feature: Add type-specific dashboard configurations and workflow automation
 * TODO: Implement type-based feature flags and permission inheritance
 */

package com.bthl.healthcare.model.enums;

import java.util.Set;

public enum UserType {
    
    ADMIN("Administrator", "System administrator with full platform access",
          Set.of("USER_MANAGEMENT", "SYSTEM_CONFIG", "AUDIT_ACCESS", "PROVIDER_MANAGEMENT",
                 "BROKER_MANAGEMENT", "COMPANY_MANAGEMENT")),
    
    BROKER("Insurance Broker", "Licensed insurance broker managing client portfolios",
          Set.of("CLIENT_MANAGEMENT", "PLAN_MANAGEMENT", "QUOTE_GENERATION",
                 "COMMISSION_TRACKING", "PROVIDER_RELATIONS")),
    
    PROVIDER("Insurance Provider", "Insurance company providing healthcare plans",
            Set.of("PLAN_MANAGEMENT", "PROVIDER_PROFILE", "BROKER_RELATIONS",
                   "UNDERWRITING", "CLAIMS_MANAGEMENT")),
    
    COMPANY_USER("Company Representative", "Company representative managing employee healthcare",
                Set.of("COMPANY_PORTFOLIO", "EMPLOYEE_MANAGEMENT", "PLAN_SELECTION",
                       "ENROLLMENT_MANAGEMENT", "BENEFITS_ADMINISTRATION"));

    public final String displayName;
    public final String description;
    public final Set<String> defaultPermissions;

    UserType(String displayName, String description, Set<String> defaultPermissions) {
        this.displayName = displayName;
        this.description = description;
        this.defaultPermissions = defaultPermissions;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean canManageUsers() {
        return this == ADMIN || this == BROKER;
    }

    public boolean handlesInsurance() {
        return this == BROKER || this == PROVIDER || this == COMPANY_USER;
    }

    public String getDashboardRoute() {
        return switch (this) {
            case ADMIN -> "/admin/dashboard";
            case BROKER -> "/broker/dashboard";
            case PROVIDER -> "/provider/dashboard";
            case COMPANY_USER -> "/company/dashboard";
        };
    }

    public String getCssClass() {
        return switch (this) {
            case ADMIN -> "user-type-admin";
            case BROKER -> "user-type-broker";
            case PROVIDER -> "user-type-provider";
            case COMPANY_USER -> "user-type-company";
        };
    }

    public String getIconClass() {
        return switch (this) {
            case ADMIN -> "fas fa-user-shield";
            case BROKER -> "fas fa-handshake";
            case PROVIDER -> "fas fa-building";
            case COMPANY_USER -> "fas fa-users";
        };
    }
}
