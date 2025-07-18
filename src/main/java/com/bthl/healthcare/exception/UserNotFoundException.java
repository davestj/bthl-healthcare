/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/exception/UserNotFoundException.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Custom exception for user not found scenarios
 */

package com.bthl.healthcare.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/exception/UserAlreadyExistsException.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Custom exception for duplicate user registration scenarios
 */

package com.bthl.healthcare.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/exception/InvalidTokenException.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Custom exception for invalid or expired token scenarios
 */

package com.bthl.healthcare.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
    
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/exception/AccountLockedException.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Custom exception for locked account scenarios
 */

package com.bthl.healthcare.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
    
    public AccountLockedException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/UserRegistrationDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Data transfer object for user registration
 */

package com.bthl.healthcare.dto;

import com.bthl.healthcare.model.enums.UserType;
import jakarta.validation.constraints.*;

public class UserRegistrationDto {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    public String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    public String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 12, message = "Password must be at least 12 characters")
    public String password;
    
    @NotBlank(message = "First name is required")
    public String firstName;
    
    @NotBlank(message = "Last name is required")
    public String lastName;
    
    public String phone;
    
    @NotNull(message = "User type is required")
    public UserType userType;
    
    public boolean autoVerify = false;
    
    // I implement getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
    
    public boolean isAutoVerify() { return autoVerify; }
    public void setAutoVerify(boolean autoVerify) { this.autoVerify = autoVerify; }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/UserUpdateDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Data transfer object for user profile updates
 */

package com.bthl.healthcare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class UserUpdateDto {
    
    public String firstName;
    public String lastName;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone must be valid")
    public String phone;
    
    public String timezone;
    public String locale;
    public String profileImageUrl;
    
    // I implement getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/dto/PasswordChangeDto.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Data transfer object for password change operations
 */

package com.bthl.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeDto {
    
    @NotBlank(message = "Current password is required")
    public String currentPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 12, message = "New password must be at least 12 characters")
    public String newPassword;
    
    @NotBlank(message = "Password confirmation is required")
    public String confirmPassword;
    
    // I implement getters and setters
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/service/EmailService.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Email service interface for notification operations
 */

package com.bthl.healthcare.service;

public interface EmailService {
    void sendEmailVerificationAsync(String email, String token);
    void sendPasswordResetEmailAsync(String email, String token);
    void sendPasswordResetConfirmationAsync(String email);
    void sendPasswordChangeNotificationAsync(String email);
    void sendAccountLockoutNotificationAsync(String email, int lockoutMinutes);
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/service/impl/EmailServiceImpl.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Email service implementation for notification operations
 */

package com.bthl.healthcare.service.impl;

import com.bthl.healthcare.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    
    public static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    @Value("${bthl.healthcare.email.enabled:false}")
    public boolean emailEnabled;
    
    @Value("${bthl.healthcare.email.from:noreply@davestj.com}")
    public String fromEmail;
    
    @Override
    @Async
    public void sendEmailVerificationAsync(String email, String token) {
        if (!emailEnabled) {
            logger.info("Email disabled - would send verification email to: {} with token: {}", email, token);
            return;
        }
        
        // I implement actual email sending logic here
        logger.info("Sending verification email to: {}", email);
    }
    
    @Override
    @Async
    public void sendPasswordResetEmailAsync(String email, String token) {
        if (!emailEnabled) {
            logger.info("Email disabled - would send password reset email to: {} with token: {}", email, token);
            return;
        }
        
        logger.info("Sending password reset email to: {}", email);
    }
    
    @Override
    @Async
    public void sendPasswordResetConfirmationAsync(String email) {
        if (!emailEnabled) {
            logger.info("Email disabled - would send password reset confirmation to: {}", email);
            return;
        }
        
        logger.info("Sending password reset confirmation to: {}", email);
    }
    
    @Override
    @Async
    public void sendPasswordChangeNotificationAsync(String email) {
        if (!emailEnabled) {
            logger.info("Email disabled - would send password change notification to: {}", email);
            return;
        }
        
        logger.info("Sending password change notification to: {}", email);
    }
    
    @Override
    @Async
    public void sendAccountLockoutNotificationAsync(String email, int lockoutMinutes) {
        if (!emailEnabled) {
            logger.info("Email disabled - would send lockout notification to: {} for {} minutes", email, lockoutMinutes);
            return;
        }
        
        logger.info("Sending account lockout notification to: {}", email);
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/service/AuditService.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Audit service interface for security and activity logging
 */

package com.bthl.healthcare.service;

import com.bthl.healthcare.model.User;
import java.util.UUID;

public interface AuditService {
    void logUserCreation(User user);
    void logUserCreationByAdmin(User user, UUID adminId);
    void logSuccessfulLogin(User user);
    void logFailedLoginAttempt(User user, String reason);
    void logPasswordChange(User user);
    void logPasswordChangeAttempt(User user, boolean success, String reason);
    void logPasswordResetRequest(User user);
    void logPasswordReset(User user);
    void logMfaEnabled(User user);
    void logMfaDisabled(User user);
    void logUserProfileUpdate(User user);
    void logUserActivation(User user, UUID activatedBy);
    void logUserDeactivation(User user, UUID deactivatedBy);
    void logAccountUnlock(User user);
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/service/impl/AuditServiceImpl.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Audit service implementation for security and activity logging
 */

package com.bthl.healthcare.service.impl;

import com.bthl.healthcare.model.User;
import com.bthl.healthcare.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditServiceImpl implements AuditService {
    
    public static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
    
    @Override
    public void logUserCreation(User user) {
        logger.info("AUDIT: User created - ID: {}, Username: {}, Type: {}", 
                   user.getId(), user.getUsername(), user.getUserType());
    }
    
    @Override
    public void logUserCreationByAdmin(User user, UUID adminId) {
        logger.info("AUDIT: User created by admin - User ID: {}, Username: {}, Admin ID: {}", 
                   user.getId(), user.getUsername(), adminId);
    }
    
    @Override
    public void logSuccessfulLogin(User user) {
        logger.info("AUDIT: Successful login - User ID: {}, Username: {}", 
                   user.getId(), user.getUsername());
    }
    
    @Override
    public void logFailedLoginAttempt(User user, String reason) {
        logger.warn("AUDIT: Failed login attempt - User ID: {}, Username: {}, Reason: {}", 
                   user.getId(), user.getUsername(), reason);
    }
    
    @Override
    public void logPasswordChange(User user) {
        logger.info("AUDIT: Password changed - User ID: {}, Username: {}", 
                   user.getId(), user.getUsername());
    }
    
    @Override
    public void logPasswordChangeAttempt(User user, boolean success, String reason) {
        if (success) {
            logger.info("AUDIT: Password change attempt successful - User ID: {}, Username: {}", 
                       user.getId(), user.getUsername());
        } else {
            logger.warn("AUDIT: Password change attempt failed - User ID: {}, Username: {}, Reason: {}", 
                       user.getId(), user.getUsername(), reason);
        }
    }
    
    @Override
    public void logPasswordResetRequest(User user) {
        logger.info("AUDIT: Password reset requested - User ID: {}, Username: {}", 
                   user.getId(), user.getUsername());
    }
    
    @Override
    public void logPasswordReset(User user) {
        logger.info("AUDIT: Password reset completed - User ID: {}, Username: {}", 
                   user.getId(), user.getUsername());
    }
    
    @Override
    public void logMfaEnabled(User user) {
        logger.info("AUDIT: MFA enabled - User ID: {}, Username: {}", 
                   user.getId(), user.getUsername());
    }
    
    @Override
    public void logMfaDisabled(User user) {
        logger.info("AUDIT: MFA disabled - User ID: {}, Username: {}", 
                   user.getId(), user.getUsername());
    }
    
    @Override
    public void logUserProfileUpdate(User user) {
        logger.info("AUDIT: User profile updated - User ID: {}, Username: {}", 
                   user.getId(), user.getUsername());
    }
    
    @Override
    public void logUserActivation(User user, UUID activatedBy) {
        logger.info("AUDIT: User activated - User ID: {}, Username: {}, Activated by: {}", 
                   user.getId(), user.getUsername(), activatedBy);
    }
    
    @Override
    public void logUserDeactivation(User user, UUID deactivatedBy) {
        logger.info("AUDIT: User deactivated - User ID: {}, Username: {}, Deactivated by: {}", 
                   user.getId(), user.getUsername(), deactivatedBy);
    }
    
    @Override
    public void logAccountUnlock(User user) {
        logger.info("AUDIT: Account unlocked - User ID: {}, Username: {}", 
                   user.getId(), user.getUsername());
    }
}
