/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/LoginRequestDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Data Transfer Object for user login authentication requests
 * Description: I created this DTO to handle login request data with proper validation for username/email
 *              and password fields. I designed this to work with my AuthController for secure authentication
 *              operations while providing comprehensive input validation and error handling.
 * 
 * Changelog:
 * 2025-07-18: Separated from AuthController.java to resolve Java compilation errors with multiple classes
 * 2025-07-16: Initial creation as part of AuthController authentication API endpoints
 * 
 * Git Commit: git commit -m "refactor: separate LoginRequestDto into individual file to resolve Java compilation errors"
 * 
 * Next Dev Feature: Add OAuth2 login support and social authentication integration
 * TODO: Implement rate limiting validation and advanced security checks for login attempts
 */

package com.bthl.healthcare.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * I designed this LoginRequestDto to encapsulate login credentials for authentication requests.
 * I've implemented validation annotations to ensure both username/email and password are provided
 * before processing authentication attempts in my healthcare platform.
 */
public class LoginRequestDto {
    
    /**
     * I use this field to accept either username or email for flexible login options.
     * I require this field to be non-blank to prevent empty authentication attempts.
     */
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;
    
    /**
     * I store the user's password securely with validation to ensure it's provided.
     * I rely on my authentication service to handle password verification and hashing.
     */
    @NotBlank(message = "Password is required")
    private String password;
    
    /**
     * I provide this default constructor for Spring framework serialization and deserialization.
     */
    public LoginRequestDto() {
        // I leave this empty for framework initialization
    }
    
    /**
     * I create this constructor for convenient object creation with all required fields.
     * 
     * @param usernameOrEmail the username or email for authentication
     * @param password the user's password
     */
    public LoginRequestDto(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }
    
    /**
     * I provide this getter to retrieve the username or email from login requests.
     * 
     * @return the username or email string
     */
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }
    
    /**
     * I use this setter to update the username or email field during request processing.
     * 
     * @param usernameOrEmail the username or email to set
     */
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
    
    /**
     * I provide this getter to retrieve the password for authentication verification.
     * 
     * @return the password string
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * I use this setter to update the password field during request processing.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * I override toString to provide meaningful string representation while excluding sensitive data.
     * I intentionally omit the password for security logging purposes.
     * 
     * @return string representation of the login request
     */
    @Override
    public String toString() {
        return "LoginRequestDto{" +
                "usernameOrEmail='" + usernameOrEmail + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
