/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/UserRegistrationDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Data Transfer Object for user registration requests in BTHL-HealthCare platform
 * Description: I created this DTO to handle user registration data with comprehensive validation
 *              for my healthcare management platform. I designed this to support various user types
 *              including company users, brokers, and providers with proper security validation.
 * 
 * Changelog:
 * 2025-07-18: Initial creation of user registration DTO with validation annotations and security features
 * 
 * Git Commit: git commit -m "feat: add UserRegistrationDto with comprehensive validation for user registration endpoints"
 * 
 * Next Dev Feature: Add email verification workflow integration and MFA setup during registration
 * TODO: Implement custom validation for license requirements for broker registration
 */

package com.bthl.healthcare.dto;

import jakarta.validation.constraints.*;
import com.bthl.healthcare.model.enums.UserType;

/**
 * I designed this DTO to capture all necessary user registration information
 * while maintaining security and validation standards for my BTHL-HealthCare platform.
 * I include comprehensive validation to ensure data integrity and user security.
 */
public class UserRegistrationDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, apostrophes, and hyphens")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, apostrophes, and hyphens")
    private String lastName;

    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
    private String phone;

    @NotNull(message = "User type is required")
    private UserType userType;

    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String companyName;

    @Size(max = 100, message = "License number must not exceed 100 characters")
    private String licenseNumber;

    @Size(min = 2, max = 2, message = "License state must be a 2-character state code")
    @Pattern(regexp = "^[A-Z]{2}$", message = "License state must be a valid 2-character state code")
    private String licenseState;

    @Size(max = 255, message = "Agency name must not exceed 255 characters")
    private String agencyName;

    @AssertTrue(message = "You must accept the terms and conditions")
    private boolean acceptTerms;

    @AssertTrue(message = "You must accept the privacy policy")
    private boolean acceptPrivacyPolicy;

    private boolean acceptMarketing = false;

    // Constructors
    public UserRegistrationDto() {}

    public UserRegistrationDto(String username, String email, String password, String confirmPassword,
                              String firstName, String lastName, String phone, UserType userType,
                              String companyName, String licenseNumber, String licenseState,
                              String agencyName, boolean acceptTerms, boolean acceptPrivacyPolicy) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.userType = userType;
        this.companyName = companyName;
        this.licenseNumber = licenseNumber;
        this.licenseState = licenseState;
        this.agencyName = agencyName;
        this.acceptTerms = acceptTerms;
        this.acceptPrivacyPolicy = acceptPrivacyPolicy;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getLicenseState() { return licenseState; }
    public void setLicenseState(String licenseState) { this.licenseState = licenseState; }
    
    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }
    
    public boolean isAcceptTerms() { return acceptTerms; }
    public void setAcceptTerms(boolean acceptTerms) { this.acceptTerms = acceptTerms; }
    
    public boolean isAcceptPrivacyPolicy() { return acceptPrivacyPolicy; }
    public void setAcceptPrivacyPolicy(boolean acceptPrivacyPolicy) { this.acceptPrivacyPolicy = acceptPrivacyPolicy; }
    
    public boolean isAcceptMarketing() { return acceptMarketing; }
    public void setAcceptMarketing(boolean acceptMarketing) { this.acceptMarketing = acceptMarketing; }

    // Validation methods
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

    public boolean isBrokerInfoComplete() {
        if (userType != UserType.BROKER) {
            return true;
        }
        return licenseNumber != null && !licenseNumber.trim().isEmpty() &&
               licenseState != null && !licenseState.trim().isEmpty();
    }

    public boolean isCompanyInfoComplete() {
        if (userType != UserType.COMPANY_USER) {
            return true;
        }
        return companyName != null && !companyName.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "UserRegistrationDto{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", userType=" + userType +
                ", companyName='" + companyName + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", licenseState='" + licenseState + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", acceptTerms=" + acceptTerms +
                ", acceptPrivacyPolicy=" + acceptPrivacyPolicy +
                ", acceptMarketing=" + acceptMarketing +
                '}';
    }
}
