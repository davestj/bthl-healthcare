/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/enums/UserStatus.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: User status enumeration for comprehensive user lifecycle management
 * Description: I designed this enumeration to handle all possible user states in my healthcare
 *              management platform, from initial registration through active use and deactivation.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of UserStatus enum with comprehensive status definitions
 * 
 * Git Commit: git commit -m "feat: add UserStatus enum for user lifecycle management"
 * 
 * Next Dev Feature: Add status transition validation and automated status change workflows
 * TODO: Implement status-based permission restrictions and audit trail integration
 */

package com.bthl.healthcare.model.enums;

/**
 * I created this UserStatus enumeration to manage the complete lifecycle of users
 * in my healthcare platform. Each status represents a distinct state with specific
 * permissions and capabilities within the system.
 */
public enum UserStatus {
    
    /**
     * I use ACTIVE status for users who have completed verification and can
     * fully access all features available to their role
     */
    ACTIVE("Active", "User account is active and fully functional"),
    
    /**
     * I use INACTIVE status for users who have been temporarily deactivated
     * but can be reactivated without losing their data or settings
     */
    INACTIVE("Inactive", "User account is temporarily inactive"),
    
    /**
     * I use SUSPENDED status for users who have been disciplinary suspended
     * due to policy violations or security concerns
     */
    SUSPENDED("Suspended", "User account is suspended due to policy violations"),
    
    /**
     * I use PENDING status for newly registered users who haven't completed
     * email verification or administrator approval process
     */
    PENDING("Pending", "User account is pending verification or approval");

    public final String displayName;
    public final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * I provide this method to check if a user can log into the system
     * based on their current status
     * 
     * @return true if user can authenticate, false otherwise
     */
    public boolean canLogin() {
        return this == ACTIVE;
    }

    /**
     * I provide this method to check if a user account can be modified
     * by administrators
     * 
     * @return true if account can be modified, false otherwise
     */
    public boolean canBeModified() {
        return this != SUSPENDED;
    }

    /**
     * I provide this method to get the CSS class for status display styling
     * 
     * @return CSS class name for frontend styling
     */
    public String getCssClass() {
        return switch (this) {
            case ACTIVE -> "status-active";
            case INACTIVE -> "status-inactive";
            case SUSPENDED -> "status-suspended";
            case PENDING -> "status-pending";
        };
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/enums/UserType.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: User type enumeration for role-based access control and user categorization
 * Description: I designed this enumeration to categorize users based on their primary function
 *              within my healthcare platform, enabling appropriate feature access and workflows.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of UserType enum with healthcare industry role definitions
 * 
 * Git Commit: git commit -m "feat: add UserType enum for healthcare role categorization"
 * 
 * Next Dev Feature: Add type-specific dashboard configurations and workflow automation
 * TODO: Implement type-based feature flags and permission inheritance
 */

package com.bthl.healthcare.model.enums;

import java.util.Set;

/**
 * I created this UserType enumeration to categorize users based on their role
 * in the healthcare ecosystem. Each type has specific permissions and access patterns
 * that I use throughout my application for authorization and feature access.
 */
public enum UserType {
    
    /**
     * I use ADMIN type for system administrators who manage the entire platform
     * including user accounts, system configuration, and global settings
     */
    ADMIN("Administrator", "System administrator with full platform access", 
          Set.of("USER_MANAGEMENT", "SYSTEM_CONFIG", "AUDIT_ACCESS", "PROVIDER_MANAGEMENT", 
                 "BROKER_MANAGEMENT", "COMPANY_MANAGEMENT")),
    
    /**
     * I use BROKER type for insurance brokers who manage client relationships
     * and facilitate insurance plan selection and enrollment
     */
    BROKER("Insurance Broker", "Licensed insurance broker managing client portfolios",
           Set.of("CLIENT_MANAGEMENT", "PLAN_MANAGEMENT", "QUOTE_GENERATION", 
                  "COMMISSION_TRACKING", "PROVIDER_RELATIONS")),
    
    /**
     * I use PROVIDER type for insurance companies and providers who offer
     * healthcare plans and manage their product portfolios
     */
    PROVIDER("Insurance Provider", "Insurance company providing healthcare plans",
             Set.of("PLAN_MANAGEMENT", "PROVIDER_PROFILE", "BROKER_RELATIONS", 
                    "UNDERWRITING", "CLAIMS_MANAGEMENT")),
    
    /**
     * I use COMPANY_USER type for representatives of companies seeking
     * healthcare coverage for their employees
     */
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

    /**
     * I provide this method to check if a user type has administrative privileges
     * 
     * @return true if user type has admin privileges, false otherwise
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * I provide this method to check if a user type can manage other users
     * 
     * @return true if user type can manage users, false otherwise
     */
    public boolean canManageUsers() {
        return this == ADMIN || this == BROKER;
    }

    /**
     * I provide this method to check if a user type works with insurance plans
     * 
     * @return true if user type handles insurance plans, false otherwise
     */
    public boolean handlesInsurance() {
        return this == BROKER || this == PROVIDER || this == COMPANY_USER;
    }

    /**
     * I provide this method to get the dashboard route for each user type
     * 
     * @return dashboard URL path for the user type
     */
    public String getDashboardRoute() {
        return switch (this) {
            case ADMIN -> "/admin/dashboard";
            case BROKER -> "/broker/dashboard";
            case PROVIDER -> "/provider/dashboard";
            case COMPANY_USER -> "/company/dashboard";
        };
    }

    /**
     * I provide this method to get the CSS class for user type styling
     * 
     * @return CSS class name for frontend styling
     */
    public String getCssClass() {
        return switch (this) {
            case ADMIN -> "user-type-admin";
            case BROKER -> "user-type-broker";
            case PROVIDER -> "user-type-provider";
            case COMPANY_USER -> "user-type-company";
        };
    }

    /**
     * I provide this method to get the icon class for user type display
     * 
     * @return icon class name for frontend display
     */
    public String getIconClass() {
        return switch (this) {
            case ADMIN -> "fas fa-user-shield";
            case BROKER -> "fas fa-handshake";
            case PROVIDER -> "fas fa-building";
            case COMPANY_USER -> "fas fa-users";
        };
    }
}
