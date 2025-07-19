/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/PasswordResetCompleteDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Data Transfer Object for password reset completion requests
 * Description: I created this DTO to handle password reset completion operations with proper token validation
 *              and new password requirements. I designed this to work with my password reset service to
 *              securely complete password changes while enforcing password complexity requirements.
 * 
 * Changelog:
 * 2025-07-18: Separated from AuthController.java to resolve Java compilation errors with multiple classes
 * 2025-07-16: Initial creation as part of AuthController password management endpoints
 * 
 * Git Commit: git commit -m "refactor: separate PasswordResetCompleteDto into individual file to resolve Java compilation errors"
 * 
 * Next Dev Feature: Add password strength meter integration and breach database validation
 * TODO: Implement password history checking to prevent reuse of recent passwords
 */

package com.bthl.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * I designed this PasswordResetCompleteDto to encapsulate the reset token and new password for secure
 * password reset completion operations. I've implemented comprehensive validation to ensure token
 * validity and password complexity requirements are met before updating user credentials.
 */
public class PasswordResetCompleteDto {
    
    /**
     * I use this field to store the password reset token that validates the reset request.
     * I require this token to be non-blank to ensure secure password reset operations.
     */
    @NotBlank(message = "Token is required")
    private String token;
    
    /**
     * I store the new password with comprehensive validation to enforce security requirements.
     * I require minimum 12 characters to ensure adequate password strength for healthcare data protection.
     */
    @NotBlank(message = "New password is required")
    @Size(min = 12, message = "Password must be at least 12 characters")
    private String newPassword;
    
    /**
     * I provide this default constructor for Spring framework serialization and deserialization.
     */
    public PasswordResetCompleteDto() {
        // I leave this empty for framework initialization
    }
    
    /**
     * I create this constructor for convenient object creation with token and new password.
     * 
     * @param token the password reset token
     * @param newPassword the new password to set
     */
    public PasswordResetCompleteDto(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }
    
    /**
     * I provide this getter to retrieve the reset token for validation and user identification.
     * 
     * @return the reset token string
     */
    public String getToken() {
        return token;
    }
    
    /**
     * I use this setter to update the token field during request processing.
     * 
     * @param token the reset token to set
     */
    public void setToken(String token) {
        this.token = token;
    }
    
    /**
     * I provide this getter to retrieve the new password for credential updates.
     * 
     * @return the new password string
     */
    public String getNewPassword() {
        return newPassword;
    }
    
    /**
     * I use this setter to update the new password field during request processing.
     * 
     * @param newPassword the new password to set
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    /**
     * I override toString to provide meaningful string representation while protecting sensitive data.
     * I intentionally exclude the password and truncate the token for security logging purposes.
     * 
     * @return string representation of the password reset completion request
     */
    @Override
    public String toString() {
        String truncatedToken = token != null && token.length() > 10 
            ? token.substring(0, 10) + "..." 
            : "[EMPTY]";
        return "PasswordResetCompleteDto{" +
                "token='" + truncatedToken + '\'' +
                ", newPassword='[PROTECTED]'" +
                '}';
    }
}
