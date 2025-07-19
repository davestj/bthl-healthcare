package com.bthl.healthcare.model.enums;

/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/enums/AuditAction.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Enum for audit action types in BTHL-HealthCare platform
 * Git Commit: git commit -m "feat: add AuditAction enum for comprehensive audit trail tracking"
 * Next Dev Feature: Add additional audit actions for healthcare-specific operations
 */

public enum AuditAction {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    LOGIN,
    LOGOUT,
    FAILED_LOGIN
}
