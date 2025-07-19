/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/UserUpdateDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Data Transfer Object for user profile update requests in BTHL-HealthCare platform
 */

package com.bthl.healthcare.dto;

import jakarta.validation.constraints.*;
import com.bthl.healthcare.model.enums.UserType;

public class UserUpdateDto {
    
    @NotNull(message = "User ID is required for updates")
    private Long userId;

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    private String username;

    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, apostrophes, and hyphens")
    private String firstName;

    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, apostrophes, and hyphens")
    private String lastName;

    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Please provide a valid phone number")
    private String phone;

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

    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z]+(/[A-Za-z_]+)*$", message = "Please provide a valid timezone identifier")
    private String timezone;

    @Size(max = 10, message = "Locale must not exceed 10 characters")
    @Pattern(regexp = "^[a-z]{2}_[A-Z]{2}$", message = "Locale must be in format: en_US, es_ES, etc.")
    private String locale;

    @Size(max = 255, message = "Profile image URL must not exceed 255 characters")
    @Pattern(regexp = "^(https?://.*\\.(jpg|jpeg|png|gif|webp))$", message = "Profile image must be a valid HTTPS URL to an image file")
    private String profileImageUrl;

    private Boolean marketingOptIn;

    // Constructors
    public UserUpdateDto() {}

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
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
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    
    public Boolean getMarketingOptIn() { return marketingOptIn; }
    public void setMarketingOptIn(Boolean marketingOptIn) { this.marketingOptIn = marketingOptIn; }

    public boolean hasUpdatableFields() {
        return username != null || email != null || firstName != null || lastName != null ||
               phone != null || userType != null || companyName != null || licenseNumber != null ||
               licenseState != null || agencyName != null || timezone != null || locale != null ||
               profileImageUrl != null || marketingOptIn != null;
    }
}
