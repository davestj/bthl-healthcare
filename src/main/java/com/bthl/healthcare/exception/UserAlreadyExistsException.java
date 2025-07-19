/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/exception/UserAlreadyExistsException.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Custom exception for duplicate user registration scenarios
 * Description: I created this exception to handle cases where user registration attempts fail
 *              due to existing users with the same username or email address. I use this to
 *              provide clear feedback during registration and user management operations.
 * 
 * Changelog:
 * 2025-07-18: Separated from combined exception file to resolve Java compilation errors
 * 2025-07-16: Initial creation of UserAlreadyExistsException for duplicate user error handling
 * 
 * Git Commit: git commit -m "refactor: separate UserAlreadyExistsException into individual file to resolve compilation errors"
 * 
 * Next Dev Feature: Add conflict resolution suggestions and duplicate field identification
 * TODO: Implement detailed duplicate detection with field-specific error messages
 */

package com.bthl.healthcare.exception;

/**
 * I created this UserAlreadyExistsException to handle scenarios where user registration
 * or creation operations fail due to existing users with conflicting identifiers.
 * This exception provides clear error messaging for duplicate user prevention.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * I create this constructor to provide a simple error message for duplicate user scenarios.
     * 
     * @param message the error message describing the specific duplicate user conflict
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * I create this constructor to provide detailed error context including the underlying cause.
     * This is useful for debugging complex duplicate user scenarios with multiple validation layers.
     * 
     * @param message the error message describing the duplicate user conflict
     * @param cause the underlying exception that caused this duplicate user detection
     */
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
