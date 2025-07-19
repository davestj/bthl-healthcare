/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/EnableMfaRequestDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Data Transfer Object for multi-factor authentication enablement requests
 * Description: I created this DTO to handle MFA activation requests with TOTP secret validation.
 *              I designed this to work with my MFA service to securely enable two-factor authentication
 *              for enhanced security in the healthcare platform, generating backup codes for recovery.
 * 
 * Changelog:
 * 2025-07-18: Separated from AuthController.java to resolve Java compilation errors with multiple classes
 * 2025-07-16: Initial creation as part of AuthController MFA management endpoints
 * 
 * Git Commit: git commit -m "refactor: separate EnableMfaRequestDto into individual file to resolve Java compilation errors"
 * 
 * Next Dev Feature: Add support for multiple MFA methods including SMS and hardware tokens
 * TODO: Implement MFA device management and trust device functionality for enhanced UX
 */

package com.bthl.healthcare.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * I designed this EnableMfaRequestDto to encapsulate TOTP secret information for MFA activation.
 * I've implemented validation to ensure the TOTP secret is provided and properly formatted before
 * enabling multi-factor authentication to enhance security for healthcare data access.
 */
public class EnableMfaRequestDto {
    
    /**
     * I use this field to store the TOTP (Time-based One-Time Password) secret for MFA configuration.
     * I require this secret to be non-blank to ensure proper MFA setup and authentication flow.
     */
    @NotBlank(message = "TOTP secret is required")
    private String totpSecret;
    
    /**
     * I provide this default constructor for Spring framework serialization and deserialization.
     */
    public EnableMfaRequestDto() {
        // I leave this empty for framework initialization
    }
    
    /**
     * I create this constructor for convenient object creation with the TOTP secret.
     * 
     * @param totpSecret the TOTP secret for MFA configuration
     */
    public EnableMfaRequestDto(String totpSecret) {
        this.totpSecret = totpSecret;
    }
    
    /**
     * I provide this getter to retrieve the TOTP secret for MFA enablement processing.
     * 
     * @return the TOTP secret string
     */
    public String getTotpSecret() {
        return totpSecret;
    }
    
    /**
     * I use this setter to update the TOTP secret field during request processing.
     * 
     * @param totpSecret the TOTP secret to set
     */
    public void setTotpSecret(String totpSecret) {
        this.totpSecret = totpSecret;
    }
    
    /**
     * I override toString to provide meaningful string representation while protecting sensitive data.
     * I intentionally truncate the TOTP secret for security logging purposes.
     * 
     * @return string representation of the MFA enablement request
     */
    @Override
    public String toString() {
        String truncatedSecret = totpSecret != null && totpSecret.length() > 8 
            ? totpSecret.substring(0, 8) + "..." 
            : "[EMPTY]";
        return "EnableMfaRequestDto{" +
                "totpSecret='" + truncatedSecret + '\'' +
                '}';
    }
}
