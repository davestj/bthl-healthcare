/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/PasswordChangeDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Data Transfer Object for password change requests in BTHL-HealthCare platform
 */

package com.bthl.healthcare.dto;

import jakarta.validation.constraints.*;

public class PasswordChangeDto {
    
    @NotNull(message = "User ID is required for password change")
    private Long userId;

    @NotBlank(message = "Current password is required for verification")
    @Size(max = 128, message = "Current password must not exceed 128 characters")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 12, max = 128, message = "New password must be between 12 and 128 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "New password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @Size(max = 128, message = "Password confirmation must not exceed 128 characters")
    private String confirmNewPassword;

    @Size(max = 255, message = "Change reason must not exceed 255 characters")
    private String changeReason;

    @Size(min = 6, max = 10, message = "MFA token must be between 6 and 10 characters")
    @Pattern(regexp = "^[0-9]+$", message = "MFA token must contain only numbers")
    private String mfaToken;

    @Size(max = 255, message = "Session ID must not exceed 255 characters")
    private String sessionId;

    @Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$", 
             message = "Please provide a valid IP address")
    private String ipAddress;

    @Size(max = 500, message = "User agent must not exceed 500 characters")
    private String userAgent;

    // Constructors
    public PasswordChangeDto() {}

    public PasswordChangeDto(Long userId, String currentPassword, String newPassword, String confirmNewPassword) {
        this.userId = userId;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    
    public String getConfirmNewPassword() { return confirmNewPassword; }
    public void setConfirmNewPassword(String confirmNewPassword) { this.confirmNewPassword = confirmNewPassword; }
    
    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
    
    public String getMfaToken() { return mfaToken; }
    public void setMfaToken(String mfaToken) { this.mfaToken = mfaToken; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    // Validation methods
    public boolean isNewPasswordConfirmed() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }

    public boolean isPasswordChanged() {
        return currentPassword != null && newPassword != null && 
               !currentPassword.equals(newPassword);
    }

    public boolean isMfaProvided() {
        return mfaToken != null && !mfaToken.trim().isEmpty();
    }

    public boolean isValidRequest() {
        return userId != null && 
               currentPassword != null && !currentPassword.trim().isEmpty() &&
               newPassword != null && !newPassword.trim().isEmpty() &&
               confirmNewPassword != null && !confirmNewPassword.trim().isEmpty() &&
               isNewPasswordConfirmed() && 
               isPasswordChanged();
    }

    @Override
    public String toString() {
        return "PasswordChangeDto{" +
                "userId=" + userId +
                ", currentPassword='[PROTECTED]'" +
                ", newPassword='[PROTECTED]'" +
                ", confirmNewPassword='[PROTECTED]'" +
                ", changeReason='" + changeReason + '\'' +
                ", mfaToken='" + (mfaToken != null ? "[PROVIDED]" : "[NOT_PROVIDED]") + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
