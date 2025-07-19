/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/exception/UserNotFoundException.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Custom exception for user not found scenarios
 * Description: I created this exception to handle cases where user lookup operations fail to find
 *              a matching user in the database. I use this for consistent error handling across
 *              my authentication and user management services.
 * 
 * Changelog:
 * 2025-07-18: Separated from combined exception file to resolve Java compilation errors
 * 2025-07-16: Initial creation of UserNotFoundException for user lookup error handling
 * 
 * Git Commit: git commit -m "refactor: separate UserNotFoundException into individual file to resolve compilation errors"
 * 
 * Next Dev Feature: Add exception context and detailed error information for debugging
 * TODO: Implement exception metrics and monitoring for user lookup failures
 */

package com.bthl.healthcare.exception;

/**
 * I created this UserNotFoundException to handle scenarios where user lookup operations
 * fail to find a matching user record in my healthcare platform database.
 * This exception provides clear error messaging for authentication and user management failures.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * I create this constructor to provide a simple error message for user not found scenarios.
     * 
     * @param message the error message describing the specific user lookup failure
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * I create this constructor to provide detailed error context including the underlying cause.
     * This is useful for debugging complex user lookup failures with multiple layers.
     * 
     * @param message the error message describing the user lookup failure
     * @param cause the underlying exception that caused this user lookup to fail
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
