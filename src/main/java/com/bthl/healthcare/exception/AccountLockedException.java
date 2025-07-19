/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/exception/AccountLockedException.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Custom exception for account lockout scenarios
 * Description: I created this exception to handle cases where user authentication fails due to
 *              account lockouts from security violations, failed login attempts, or administrative
 *              suspension in my healthcare platform's security system.
 * 
 * Changelog:
 * 2025-07-18: Separated from combined exception file to resolve Java compilation errors
 * 2025-07-16: Initial creation of AccountLockedException for account security error handling
 * 
 * Git Commit: git commit -m "refactor: separate AccountLockedException into individual file to resolve compilation errors"
 * 
 * Next Dev Feature: Add lockout duration and unlock condition details in error messages
 * TODO: Implement automated lockout resolution and security incident tracking
 */

package com.bthl.healthcare.exception;

/**
 * I created this AccountLockedException to handle scenarios where user authentication
 * is blocked due to account security lockouts in my healthcare platform.
 * This exception provides clear messaging about account status without exposing security details.
 */
public class AccountLockedException extends RuntimeException {

    /**
     * I create this constructor to provide a simple error message for account lockout scenarios.
     * 
     * @param message the error message describing the specific account lockout reason
     */
    public AccountLockedException(String message) {
        super(message);
    }

    /**
     * I create this constructor to provide detailed error context including the underlying cause.
     * This is useful for debugging complex account lockout scenarios with multiple security triggers.
     * 
     * @param message the error message describing the account lockout
     * @param cause the underlying exception that caused this account lockout detection
     */
    public AccountLockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
