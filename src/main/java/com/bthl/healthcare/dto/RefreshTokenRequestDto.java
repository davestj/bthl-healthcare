/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/RefreshTokenRequestDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Data Transfer Object for JWT refresh token requests
 * Description: I created this DTO to handle refresh token requests for extending user authentication sessions.
 *              I designed this to work with my JWT token provider to securely refresh access tokens without
 *              requiring users to re-authenticate, improving user experience while maintaining security.
 * 
 * Changelog:
 * 2025-07-18: Separated from AuthController.java to resolve Java compilation errors with multiple classes
 * 2025-07-16: Initial creation as part of AuthController JWT token management endpoints
 * 
 * Git Commit: git commit -m "refactor: separate RefreshTokenRequestDto into individual file to resolve Java compilation errors"
 * 
 * Next Dev Feature: Add token rotation and refresh token blacklisting for enhanced security
 * TODO: Implement refresh token fingerprinting and device tracking for fraud detection
 */

package com.bthl.healthcare.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * I designed this RefreshTokenRequestDto to encapsulate refresh token data for JWT token renewal requests.
 * I've implemented validation to ensure the refresh token is provided and properly formatted before
 * attempting to generate new access tokens in my healthcare platform authentication system.
 */
public class RefreshTokenRequestDto {
    
    /**
     * I use this field to store the refresh token that will be validated and used to generate new access tokens.
     * I require this field to be non-blank to ensure valid refresh token operations.
     */
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    
    /**
     * I provide this default constructor for Spring framework serialization and deserialization.
     */
    public RefreshTokenRequestDto() {
        // I leave this empty for framework initialization
    }
    
    /**
     * I create this constructor for convenient object creation with the refresh token.
     * 
     * @param refreshToken the JWT refresh token string
     */
    public RefreshTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    /**
     * I provide this getter to retrieve the refresh token for JWT validation and renewal.
     * 
     * @return the refresh token string
     */
    public String getRefreshToken() {
        return refreshToken;
    }
    
    /**
     * I use this setter to update the refresh token field during request processing.
     * 
     * @param refreshToken the refresh token to set
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    /**
     * I override toString to provide meaningful string representation while protecting sensitive token data.
     * I intentionally truncate the token for security logging purposes.
     * 
     * @return string representation of the refresh token request
     */
    @Override
    public String toString() {
        String truncatedToken = refreshToken != null && refreshToken.length() > 20 
            ? refreshToken.substring(0, 20) + "..." 
            : "[EMPTY]";
        return "RefreshTokenRequestDto{" +
                "refreshToken='" + truncatedToken + '\'' +
                '}';
    }
}
