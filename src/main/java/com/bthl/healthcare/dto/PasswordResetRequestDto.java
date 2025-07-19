/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/PasswordResetRequestDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Data Transfer Object for password reset initiation requests
 * Description: I created this DTO to handle password reset initiation requests with email validation.
 *              I designed this to work with my password reset service to securely generate and send
 *              reset tokens while preventing email enumeration attacks through consistent responses.
 * 
 * Changelog:
 * 2025-07-18: Separated from AuthController.java to resolve Java compilation errors with multiple classes
 * 2025-07-16: Initial creation as part of AuthController password management endpoints
 * 
 * Git Commit: git commit -m "refactor: separate PasswordResetRequestDto into individual file to resolve Java compilation errors"
 * 
 * Next Dev Feature: Add password reset analytics and security monitoring for suspicious reset patterns
 * TODO: Implement CAPTCHA integration and rate limiting for password reset requests
 */

package com.bthl.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

/**
 * I designed this PasswordResetRequestDto to encapsulate email information for password reset initiation.
 * I've implemented comprehensive email validation to ensure proper format and prevent malformed requests
 * that could interfere with my password reset security mechanisms in the healthcare platform.
 */
public class PasswordResetRequestDto {
    
    /**
     * I use this field to store the email address for password reset token delivery.
     * I require proper email validation to ensure successful password reset communication.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    /**
     * I provide this default constructor for Spring framework serialization and deserialization.
     */
    public PasswordResetRequestDto() {
        // I leave this empty for framework initialization
    }
    
    /**
     * I create this constructor for convenient object creation with the email address.
     * 
     * @param email the email address for password reset
     */
    public PasswordResetRequestDto(String email) {
        this.email = email;
    }
    
    /**
     * I provide this getter to retrieve the email address for password reset processing.
     * 
     * @return the email address string
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * I use this setter to update the email field during request processing.
     * 
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * I override toString to provide meaningful string representation of the password reset request.
     * I include the email for debugging purposes while maintaining appropriate security logging.
     * 
     * @return string representation of the password reset request
     */
    @Override
    public String toString() {
        return "PasswordResetRequestDto{" +
                "email='" + email + '\'' +
                '}';
    }
}
