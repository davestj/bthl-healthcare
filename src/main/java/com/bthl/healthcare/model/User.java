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
    private UUID id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Size(min = 12, message = "Password must be at least 12 characters long")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, apostrophes, and hyphens")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, apostrophes, and hyphens")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in valid international format")
    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "mfa_enabled", nullable = false)
    private Boolean mfaEnabled = false;

    @JsonIgnore
    @Column(name = "mfa_secret", length = 255)
    private String mfaSecret;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "user_backup_codes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "backup_code")
    private Set<String> backupCodes = new HashSet<>();

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @JsonIgnore
    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;

    @JsonIgnore
    @Column(name = "password_reset_expires")
    private LocalDateTime passwordResetExpires;

    @JsonIgnore
    @Column(name = "email_verification_token", length = 255)
    private String emailVerificationToken;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "timezone", length = 50)
    private String timezone = "UTC";

    @Column(name = "locale", length = 10)
    private String locale = "en_US";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by")
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    public User() {
    }

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

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
            if (role.getPermissions() != null) {
                role.getPermissions().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission.toString())));
            }
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
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return accountLockedUntil == null || LocalDateTime.now().isAfter(accountLockedUntil);
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE && Boolean.TRUE.equals(emailVerified);
    }

    // Convenience getters
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
        return "" + firstName.charAt(0) + lastName.charAt(0);
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

    // Business logic methods
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

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public UserStatus getStatus() { return status; }
    public UserType getUserType() { return userType; }
    public Role getRole() { return role; }
    public Boolean getMfaEnabled() { return mfaEnabled; }
    public String getMfaSecret() { return mfaSecret; }
    public Set<String> getBackupCodes() { return backupCodes; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public LocalDateTime getAccountLockedUntil() { return accountLockedUntil; }
    public String getPasswordResetToken() { return passwordResetToken; }
    public LocalDateTime getPasswordResetExpires() { return passwordResetExpires; }
    public String getEmailVerificationToken() { return emailVerificationToken; }
    public Boolean getEmailVerified() { return emailVerified; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getTimezone() { return timezone; }
    public String getLocale() { return locale; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public UUID getCreatedBy() { return createdBy; }
    public UUID getUpdatedBy() { return updatedBy; }
    public String getPasswordHash() { return passwordHash; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setUserType(UserType userType) { this.userType = userType; }
    public void setRole(Role role) { this.role = role; }
    public void setMfaEnabled(Boolean mfaEnabled) { this.mfaEnabled = mfaEnabled; }
    public void setMfaSecret(String mfaSecret) { this.mfaSecret = mfaSecret; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) { this.accountLockedUntil = accountLockedUntil; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }
    public void setPasswordResetExpires(LocalDateTime passwordResetExpires) { this.passwordResetExpires = passwordResetExpires; }
    public void setEmailVerificationToken(String emailVerificationToken) { this.emailVerificationToken = emailVerificationToken; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public void setLocale(String locale) { this.locale = locale; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }

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
