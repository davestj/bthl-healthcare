/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/exception/InvalidTokenException.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Custom exception for invalid token scenarios
 * Description: I created this exception to handle cases where token validation fails due to
 *              expired, malformed, or invalid tokens used in authentication, password reset,
 *              or email verification operations throughout my healthcare platform.
 * 
 * Changelog:
 * 2025-07-18: Separated from combined exception file to resolve Java compilation errors
 * 2025-07-16: Initial creation of InvalidTokenException for token validation error handling
 * 
 * Git Commit: git commit -m "refactor: separate InvalidTokenException into individual file to resolve compilation errors"
 * 
 * Next Dev Feature: Add token type identification and expiration details in error messages
 * TODO: Implement token forensics and security event logging for invalid token attempts
 */

package com.bthl.healthcare.exception;

/**
 * I created this InvalidTokenException to handle scenarios where token validation
 * operations fail due to expired, malformed, or fraudulent tokens in my healthcare platform.
 * This exception provides secure error messaging without exposing sensitive token details.
 */
public class InvalidTokenException extends RuntimeException {

    /**
     * I create this constructor to provide a simple error message for invalid token scenarios.
     * 
     * @param message the error message describing the specific token validation failure
     */
    public InvalidTokenException(String message) {
        super(message);
    }

    /**
     * I create this constructor to provide detailed error context including the underlying cause.
     * This is useful for debugging complex token validation failures with multiple security layers.
     * 
     * @param message the error message describing the token validation failure
     * @param cause the underlying exception that caused this token validation to fail
     */
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
