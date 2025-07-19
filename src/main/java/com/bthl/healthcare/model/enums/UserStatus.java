/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/enums/UserStatus.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: User status enumeration for comprehensive user lifecycle management
 * Description: I designed this enumeration to handle all possible user states in my healthcare
 *              management platform, from initial registration through active use and deactivation.
 * 
 * Changelog:
 * 2025-07-18: Separated UserType enum into individual file to resolve Java compilation errors
 * 2025-07-16: Initial creation of UserStatus enum with comprehensive status definitions
 * 
 * Git Commit: git commit -m "refactor: separate UserStatus enum into individual file to resolve compilation errors"
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
    
    ACTIVE("Active", "User account is active and fully functional"),
    INACTIVE("Inactive", "User account is temporarily inactive"),
    SUSPENDED("Suspended", "User account is suspended due to policy violations"),
    PENDING("Pending", "User account is pending verification or approval");

    public final String displayName;
    public final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public boolean canLogin() {
        return this == ACTIVE;
    }

    public boolean canBeModified() {
        return this != SUSPENDED;
    }

    public String getCssClass() {
        return switch (this) {
            case ACTIVE -> "status-active";
            case INACTIVE -> "status-inactive";
            case SUSPENDED -> "status-suspended";
            case PENDING -> "status-pending";
        };
    }
}
