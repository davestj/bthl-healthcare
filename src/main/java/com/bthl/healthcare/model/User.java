/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/User.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: User entity model for comprehensive user management and authentication
 * Description: I designed this User entity to handle all aspects of user management including
 *              authentication, authorization, MFA, session tracking, and audit logging.
 *              I've implemented comprehensive validation and security features.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of User entity with comprehensive security and validation features
 * 
 * Git Commit: git commit -m "feat: add User entity model with MFA, validation, and audit support"
 * 
 * Next Dev Feature: Add user preference management and customizable dashboard configurations
 * TODO: Implement user activity analytics and behavioral pattern recognition
 */

package com.bthl.healthcare.model;

import com.bthl.healthcare.model.enums.UserStatus;
import com.bthl.healthcare.model.enums.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

/**
 * I created this User entity as the central authentication and authorization model
 * for my healthcare platform. I've implemented Spring Security UserDetails interface
 * for seamless integration with Spring Security framework.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_username", columnList = "username"),
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_status", columnList = "status"),
    @Index(name = "idx_users_type", columnList = "user_type"),
    @Index(name = "idx_users_last_login", columnList = "last_login")
})
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    @Column(name = "username", unique = true, nullable = false, length = 100)
    public String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", unique = true, nullable = false, length = 255)
    public String email;

    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Size(min = 12, message = "Password must be at least 12 characters long")
    @Column(name = "password_hash", nullable = false, length = 255)
    public String passwordHash;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, apostrophes, and hyphens")
    @Column(name = "first_name", nullable = false, length = 100)
    public String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, apostrophes, and hyphens")
    @Column(name = "last_name", nullable = false, length = 100)
    public String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in valid international format")
    @Column(name = "phone", length = 20)
    public String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public UserStatus status = UserStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    public UserType userType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    public Role role;

    @Column(name = "mfa_enabled", nullable = false)
    public Boolean mfaEnabled = false;

    @JsonIgnore
    @Column(name = "mfa_secret", length = 255)
    public String mfaSecret;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "user_backup_codes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "backup_code")
    public Set<String> backupCodes = new HashSet<>();

    @Column(name = "last_login")
    public LocalDateTime lastLogin;

    @Column(name = "failed_login_attempts", nullable = false)
    public Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    public LocalDateTime accountLockedUntil;

    @JsonIgnore
    @Column(name = "password_reset_token", length = 255)
    public String passwordResetToken;

    @JsonIgnore
    @Column(name = "password_reset_expires")
    public LocalDateTime passwordResetExpires;

    @JsonIgnore
    @Column(name = "email_verification_token", length = 255)
    public String emailVerificationToken;

    @Column(name = "email_verified", nullable = false)
    public Boolean emailVerified = false;

    @Column(name = "profile_image_url", length = 255)
    public String profileImageUrl;

    @Column(name = "timezone", length = 50)
    public String timezone = "UTC";

    @Column(name = "locale", length = 10)
    public String locale = "en_US";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by")
    public UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    public UUID updatedBy;

    // I implement default constructor for JPA
    public User() {}

    // I create a comprehensive constructor for user creation
    public User(String username, String email, String passwordHash, String firstName, 
                String lastName, UserType userType, Role role) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.role = role;
        this.status = UserStatus.PENDING;
        this.mfaEnabled = false;
        this.failedLoginAttempts = 0;
        this.emailVerified = false;
    }

    // I implement Spring Security UserDetails interface methods

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // I convert role permissions to Spring Security authorities
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // I add the role as an authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
        
        // I add individual permissions as authorities
        if (role.getPermissions() != null) {
            role.getPermissions().forEach(permission -> 
                authorities.add(new SimpleGrantedAuthority(permission.toString())));
        }
        
        return authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        // I consider accounts as non-expired unless explicitly marked
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        // I check if account is currently locked based on lockout timestamp
        return accountLockedUntil == null || LocalDateTime.now().isAfter(accountLockedUntil);
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        // I implement password expiration logic here if needed
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        // I consider user enabled if status is ACTIVE and email is verified
        return status == UserStatus.ACTIVE && emailVerified;
    }

    // I create convenient getter methods for computed properties

    @JsonProperty("fullName")
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        return firstName + " " + lastName.charAt(0) + ".";
    }

    @JsonProperty("initials")
    public String getInitials() {
        return String.valueOf(firstName.charAt(0)) + String.valueOf(lastName.charAt(0));
    }

    @JsonProperty("isLocked")
    public boolean getIsLocked() {
        return !isAccountNonLocked();
    }

    @JsonProperty("hasProfileImage")
    public boolean getHasProfileImage() {
        return profileImageUrl != null && !profileImageUrl.trim().isEmpty();
    }

    @JsonProperty("daysSinceLastLogin")
    public Long getDaysSinceLastLogin() {
        if (lastLogin == null) return null;
        return java.time.Duration.between(lastLogin, LocalDateTime.now()).toDays();
    }

    // I implement business logic methods for user management

    public void lockAccount(int lockoutMinutes) {
        this.accountLockedUntil = LocalDateTime.now().plusMinutes(lockoutMinutes);
    }

    public void unlockAccount() {
        this.accountLockedUntil = null;
        this.failedLoginAttempts = 0;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts = (this.failedLoginAttempts == null ? 0 : this.failedLoginAttempts) + 1;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
        resetFailedLoginAttempts();
    }

    public void enableMfa(String secret) {
        this.mfaSecret = secret;
        this.mfaEnabled = true;
    }

    public void disableMfa() {
        this.mfaSecret = null;
        this.mfaEnabled = false;
        this.backupCodes.clear();
    }

    public void setBackupCodes(Set<String> codes) {
        this.backupCodes = new HashSet<>(codes);
    }

    public boolean useBackupCode(String code) {
        return this.backupCodes.remove(code);
    }

    public void setPasswordResetToken(String token, int expiryHours) {
        this.passwordResetToken = token;
        this.passwordResetExpires = LocalDateTime.now().plusHours(expiryHours);
    }

    public void clearPasswordResetToken() {
        this.passwordResetToken = null;
        this.passwordResetExpires = null;
    }

    public boolean isPasswordResetTokenValid() {
        return passwordResetToken != null && 
               passwordResetExpires != null && 
               LocalDateTime.now().isBefore(passwordResetExpires);
    }

    public void setEmailVerificationToken(String token) {
        this.emailVerificationToken = token;
        this.emailVerified = false;
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerificationToken = null;
        if (this.status == UserStatus.PENDING) {
            this.status = UserStatus.ACTIVE;
        }
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }

    // I implement equals and hashCode based on UUID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // I provide a comprehensive toString method for debugging
    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", status=" + status +
               ", userType=" + userType +
               ", mfaEnabled=" + mfaEnabled +
               ", emailVerified=" + emailVerified +
               ", createdAt=" + createdAt +
               '}';
    }
}
