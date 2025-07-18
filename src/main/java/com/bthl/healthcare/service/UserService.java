/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/service/UserService.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: User service implementation for comprehensive user management business logic
 * Description: I designed this service to handle all user-related business operations including
 *              registration, authentication, authorization, MFA management, and user lifecycle.
 *              I've implemented comprehensive security measures and validation throughout.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of UserService with comprehensive user management capabilities
 * 
 * Git Commit: git commit -m "feat: add UserService with comprehensive user management and security features"
 * 
 * Next Dev Feature: Add user analytics service methods and advanced user preference management
 * TODO: Implement user behavior analysis and automated security monitoring
 */

package com.bthl.healthcare.service;

import com.bthl.healthcare.model.User;
import com.bthl.healthcare.model.Role;
import com.bthl.healthcare.model.enums.UserStatus;
import com.bthl.healthcare.model.enums.UserType;
import com.bthl.healthcare.repository.UserRepository;
import com.bthl.healthcare.repository.RoleRepository;
import com.bthl.healthcare.exception.UserNotFoundException;
import com.bthl.healthcare.exception.UserAlreadyExistsException;
import com.bthl.healthcare.exception.InvalidTokenException;
import com.bthl.healthcare.exception.AccountLockedException;
import com.bthl.healthcare.dto.UserRegistrationDto;
import com.bthl.healthcare.dto.UserUpdateDto;
import com.bthl.healthcare.dto.PasswordChangeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

/**
 * I created this UserService class to handle all user-related business logic
 * in my healthcare platform. I've implemented comprehensive security measures,
 * validation, and user lifecycle management with proper transaction handling.
 */
@Service
@Transactional
public class UserService {

    public static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public final UserRepository userRepository;
    public final RoleRepository roleRepository;
    public final PasswordEncoder passwordEncoder;
    public final EmailService emailService;
    public final AuditService auditService;

    // I inject configuration values for security settings
    @Value("${bthl.healthcare.security.lockout.max-attempts:5}")
    public int maxFailedAttempts;

    @Value("${bthl.healthcare.security.lockout.lockout-duration-minutes:30}")
    public int lockoutDurationMinutes;

    @Value("${bthl.healthcare.security.password.min-length:12}")
    public int passwordMinLength;

    @Value("${bthl.healthcare.mfa.backup-codes-count:10}")
    public int backupCodesCount;

    public SecureRandom secureRandom = new SecureRandom();

    /**
     * I create this constructor to inject all required dependencies for user management.
     * I use constructor injection for better testability and dependency management.
     */
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder, EmailService emailService,
                      AuditService auditService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.auditService = auditService;
    }

    // I implement user registration and creation methods

    /**
     * I register a new user in the system with comprehensive validation.
     * This method handles the complete user registration workflow including
     * email verification token generation and welcome email sending.
     * 
     * @param registrationDto the user registration data
     * @return the created user entity
     * @throws UserAlreadyExistsException if username or email already exists
     */
    public User registerUser(UserRegistrationDto registrationDto) {
        logger.debug("Registering new user with username: {}", registrationDto.getUsername());

        // I validate that username and email are unique
        validateUniqueUser(registrationDto.getUsername(), registrationDto.getEmail());

        // I find the appropriate role for the user type
        Role userRole = findRoleByUserType(registrationDto.getUserType());
        if (userRole == null) {
            throw new IllegalArgumentException("No role found for user type: " + registrationDto.getUserType());
        }

        // I create the new user entity
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhone(registrationDto.getPhone());
        user.setUserType(registrationDto.getUserType());
        user.setRole(userRole);
        user.setStatus(UserStatus.PENDING);
        user.setEmailVerified(false);
        user.setMfaEnabled(false);
        user.setFailedLoginAttempts(0);

        // I generate an email verification token
        String verificationToken = generateSecureToken();
        user.setEmailVerificationToken(verificationToken);

        // I save the user and send verification email
        User savedUser = userRepository.save(user);
        auditService.logUserCreation(savedUser);

        // I send welcome and verification email asynchronously
        emailService.sendEmailVerificationAsync(savedUser.getEmail(), verificationToken);

        logger.info("Successfully registered user: {} with ID: {}", savedUser.getUsername(), savedUser.getId());
        return savedUser;
    }

    /**
     * I create a user account for administrative purposes.
     * This method allows administrators to create user accounts directly.
     * 
     * @param userDto the user creation data
     * @param createdBy the UUID of the administrator creating the user
     * @return the created user entity
     */
    public User createUser(UserRegistrationDto userDto, UUID createdBy) {
        logger.debug("Admin creating user with username: {} by admin: {}", userDto.getUsername(), createdBy);

        User user = registerUser(userDto);
        
        // I set the creator for audit purposes
        user.setCreatedBy(createdBy);
        
        // I can optionally auto-verify for admin-created accounts
        if (userDto.isAutoVerify()) {
            user.verifyEmail();
            user.activate();
        }

        User savedUser = userRepository.save(user);
        auditService.logUserCreationByAdmin(savedUser, createdBy);

        return savedUser;
    }

    // I implement user authentication and security methods

    /**
     * I authenticate a user with username/email and password.
     * This method handles failed login attempts and account lockout logic.
     * 
     * @param identifier the username or email
     * @param password the plain text password
     * @return the authenticated user
     * @throws UserNotFoundException if user doesn't exist
     * @throws AccountLockedException if account is locked
     */
    public User authenticateUser(String identifier, String password) {
        logger.debug("Authenticating user with identifier: {}", identifier);

        // I find the user by username or email
        User user = findUserByUsernameOrEmail(identifier);
        
        // I check if account is locked
        if (user.getAccountLockedUntil() != null && 
            LocalDateTime.now().isBefore(user.getAccountLockedUntil())) {
            auditService.logFailedLoginAttempt(user, "Account locked");
            throw new AccountLockedException("Account is locked until: " + user.getAccountLockedUntil());
        }

        // I verify the password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            handleFailedLogin(user);
            auditService.logFailedLoginAttempt(user, "Invalid password");
            throw new IllegalArgumentException("Invalid credentials");
        }

        // I check if user account is enabled
        if (!user.isEnabled()) {
            auditService.logFailedLoginAttempt(user, "Account disabled");
            throw new IllegalArgumentException("Account is not enabled");
        }

        // I handle successful login
        handleSuccessfulLogin(user);
        auditService.logSuccessfulLogin(user);

        logger.info("Successfully authenticated user: {}", user.getUsername());
        return user;
    }

    /**
     * I handle failed login attempts and implement account lockout logic.
     * 
     * @param user the user who failed authentication
     */
    public void handleFailedLogin(User user) {
        user.incrementFailedLoginAttempts();
        
        if (user.getFailedLoginAttempts() >= maxFailedAttempts) {
            user.lockAccount(lockoutDurationMinutes);
            logger.warn("Locked account for user: {} due to {} failed attempts", 
                       user.getUsername(), user.getFailedLoginAttempts());
            
            // I send account lockout notification
            emailService.sendAccountLockoutNotificationAsync(user.getEmail(), lockoutDurationMinutes);
        }
        
        userRepository.save(user);
    }

    /**
     * I handle successful login by updating login timestamp and resetting failed attempts.
     * 
     * @param user the successfully authenticated user
     */
    public void handleSuccessfulLogin(User user) {
        user.updateLastLogin();
        userRepository.updateLastLogin(user.getId(), LocalDateTime.now());
    }

    // I implement password management methods

    /**
     * I change a user's password with proper validation and security measures.
     * 
     * @param userId the UUID of the user
     * @param passwordChangeDto the password change data
     * @throws UserNotFoundException if user doesn't exist
     */
    public void changePassword(UUID userId, PasswordChangeDto passwordChangeDto) {
        logger.debug("Changing password for user: {}", userId);

        User user = findUserById(userId);
        
        // I verify the current password
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPasswordHash())) {
            auditService.logPasswordChangeAttempt(user, false, "Invalid current password");
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // I validate the new password
        validatePassword(passwordChangeDto.getNewPassword());

        // I update the password
        user.setPasswordHash(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        user.clearPasswordResetToken();
        
        userRepository.save(user);
        auditService.logPasswordChange(user);

        // I send password change notification
        emailService.sendPasswordChangeNotificationAsync(user.getEmail());

        logger.info("Successfully changed password for user: {}", user.getUsername());
    }

    /**
     * I initiate a password reset process by generating and sending a reset token.
     * 
     * @param email the email address of the user requesting password reset
     */
    public void initiatePasswordReset(String email) {
        logger.debug("Initiating password reset for email: {}", email);

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            // I log the attempt but don't reveal if email exists
            logger.warn("Password reset requested for non-existent email: {}", email);
            return;
        }

        User user = userOpt.get();
        String resetToken = generateSecureToken();
        user.setPasswordResetToken(resetToken, 24); // 24 hours expiry

        userRepository.save(user);
        auditService.logPasswordResetRequest(user);

        // I send password reset email
        emailService.sendPasswordResetEmailAsync(user.getEmail(), resetToken);

        logger.info("Password reset initiated for user: {}", user.getUsername());
    }

    /**
     * I complete a password reset using a valid reset token.
     * 
     * @param token the password reset token
     * @param newPassword the new password
     * @throws InvalidTokenException if token is invalid or expired
     */
    public void completePasswordReset(String token, String newPassword) {
        logger.debug("Completing password reset with token");

        User user = userRepository.findByPasswordResetToken(token)
            .orElseThrow(() -> new InvalidTokenException("Invalid or expired password reset token"));

        if (!user.isPasswordResetTokenValid()) {
            throw new InvalidTokenException("Password reset token has expired");
        }

        // I validate the new password
        validatePassword(newPassword);

        // I update the password and clear reset token
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.clearPasswordResetToken();
        user.resetFailedLoginAttempts();
        user.unlockAccount();

        userRepository.save(user);
        auditService.logPasswordReset(user);

        // I send password reset confirmation
        emailService.sendPasswordResetConfirmationAsync(user.getEmail());

        logger.info("Successfully completed password reset for user: {}", user.getUsername());
    }

    // I implement MFA management methods

    /**
     * I enable multi-factor authentication for a user.
     * 
     * @param userId the UUID of the user
     * @param totpSecret the TOTP secret for MFA
     * @return the generated backup codes
     */
    public Set<String> enableMfa(UUID userId, String totpSecret) {
        logger.debug("Enabling MFA for user: {}", userId);

        User user = findUserById(userId);
        
        // I generate backup codes
        Set<String> backupCodes = generateBackupCodes();
        
        user.enableMfa(totpSecret);
        user.setBackupCodes(backupCodes);
        
        userRepository.save(user);
        auditService.logMfaEnabled(user);

        logger.info("Successfully enabled MFA for user: {}", user.getUsername());
        return backupCodes;
    }

    /**
     * I disable multi-factor authentication for a user.
     * 
     * @param userId the UUID of the user
     */
    public void disableMfa(UUID userId) {
        logger.debug("Disabling MFA for user: {}", userId);

        User user = findUserById(userId);
        user.disableMfa();
        
        userRepository.save(user);
        auditService.logMfaDisabled(user);

        logger.info("Successfully disabled MFA for user: {}", user.getUsername());
    }

    // I implement user management methods

    /**
     * I update user profile information with validation.
     * 
     * @param userId the UUID of the user to update
     * @param updateDto the update data
     * @return the updated user
     */
    public User updateUserProfile(UUID userId, UserUpdateDto updateDto) {
        logger.debug("Updating profile for user: {}", userId);

        User user = findUserById(userId);
        
        // I update allowed fields
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        if (updateDto.getTimezone() != null) {
            user.setTimezone(updateDto.getTimezone());
        }
        if (updateDto.getLocale() != null) {
            user.setLocale(updateDto.getLocale());
        }
        if (updateDto.getProfileImageUrl() != null) {
            user.setProfileImageUrl(updateDto.getProfileImageUrl());
        }

        User savedUser = userRepository.save(user);
        auditService.logUserProfileUpdate(user);

        logger.info("Successfully updated profile for user: {}", user.getUsername());
        return savedUser;
    }

    /**
     * I activate a user account administratively.
     * 
     * @param userId the UUID of the user to activate
     * @param activatedBy the UUID of the administrator
     */
    public void activateUser(UUID userId, UUID activatedBy) {
        logger.debug("Activating user: {} by admin: {}", userId, activatedBy);

        User user = findUserById(userId);
        user.activate();
        user.setUpdatedBy(activatedBy);
        
        userRepository.save(user);
        auditService.logUserActivation(user, activatedBy);

        logger.info("Successfully activated user: {}", user.getUsername());
    }

    /**
     * I deactivate a user account administratively.
     * 
     * @param userId the UUID of the user to deactivate
     * @param deactivatedBy the UUID of the administrator
     */
    public void deactivateUser(UUID userId, UUID deactivatedBy) {
        logger.debug("Deactivating user: {} by admin: {}", userId, deactivatedBy);

        User user = findUserById(userId);
        user.deactivate();
        user.setUpdatedBy(deactivatedBy);
        
        userRepository.save(user);
        auditService.logUserDeactivation(user, deactivatedBy);

        logger.info("Successfully deactivated user: {}", user.getUsername());
    }

    // I implement query and search methods

    /**
     * I find a user by their ID with proper error handling.
     * 
     * @param userId the UUID of the user
     * @return the user entity
     * @throws UserNotFoundException if user doesn't exist
     */
    @Transactional(readOnly = true)
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    /**
     * I find a user by username or email for authentication.
     * 
     * @param identifier the username or email
     * @return the user entity
     * @throws UserNotFoundException if user doesn't exist
     */
    @Transactional(readOnly = true)
    public User findUserByUsernameOrEmail(String identifier) {
        // I try username first, then email
        Optional<User> userOpt = userRepository.findByUsername(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmailIgnoreCase(identifier);
        }
        
        return userOpt.orElseThrow(() -> 
            new UserNotFoundException("User not found with identifier: " + identifier));
    }

    /**
     * I search users with pagination and filtering.
     * 
     * @param searchTerm the search term for names, username, or email
     * @param pageable pagination information
     * @return page of matching users
     */
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.searchUsers(searchTerm.trim(), pageable);
    }

    /**
     * I get users by status with pagination.
     * 
     * @param status the user status to filter by
     * @param pageable pagination information
     * @return page of users with the specified status
     */
    @Transactional(readOnly = true)
    public Page<User> getUsersByStatus(UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }

    /**
     * I get users by type with pagination.
     * 
     * @param userType the user type to filter by
     * @param pageable pagination information
     * @return page of users with the specified type
     */
    @Transactional(readOnly = true)
    public Page<User> getUsersByType(UserType userType, Pageable pageable) {
        return userRepository.findByUserType(userType, pageable);
    }

    // I implement utility and helper methods

    /**
     * I validate that a username and email are unique in the system.
     * 
     * @param username the username to check
     * @param email the email to check
     * @throws UserAlreadyExistsException if either already exists
     */
    public void validateUniqueUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already exists: " + email);
        }
    }

    /**
     * I validate password strength according to security requirements.
     * 
     * @param password the password to validate
     * @throws IllegalArgumentException if password doesn't meet requirements
     */
    public void validatePassword(String password) {
        if (password == null || password.length() < passwordMinLength) {
            throw new IllegalArgumentException("Password must be at least " + passwordMinLength + " characters long");
        }
        
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new IllegalArgumentException("Password must contain uppercase, lowercase, number, and special character");
        }
    }

    /**
     * I generate a secure random token for various purposes.
     * 
     * @return a secure random token string
     */
    public String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * I generate backup codes for MFA.
     * 
     * @return a set of backup codes
     */
    public Set<String> generateBackupCodes() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < backupCodesCount; i++) {
            byte[] codeBytes = new byte[6];
            secureRandom.nextBytes(codeBytes);
            String code = Base64.getEncoder().encodeToString(codeBytes).substring(0, 8);
            codes.add(code);
        }
        return codes;
    }

    /**
     * I find the appropriate role for a user type.
     * 
     * @param userType the user type
     * @return the corresponding role
     */
    public Role findRoleByUserType(UserType userType) {
        return roleRepository.findByName(userType.name())
            .orElse(roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default USER role not found")));
    }

    /**
     * I perform cleanup operations for expired tokens and locked accounts.
     * This method should be called periodically by a scheduled task.
     */
    @Transactional
    public void performSecurityCleanup() {
        logger.debug("Performing security cleanup operations");

        LocalDateTime cutoffTime = LocalDateTime.now();
        
        // I clean up expired password reset tokens
        int clearedTokens = userRepository.clearExpiredPasswordResetTokens(cutoffTime);
        if (clearedTokens > 0) {
            logger.info("Cleared {} expired password reset tokens", clearedTokens);
        }

        // I unlock accounts that have passed their lockout period
        List<User> lockedUsers = userRepository.findLockedUsers(cutoffTime);
        for (User user : lockedUsers) {
            if (user.getAccountLockedUntil().isBefore(cutoffTime)) {
                userRepository.unlockUserAccount(user.getId());
                auditService.logAccountUnlock(user);
            }
        }
        
        if (!lockedUsers.isEmpty()) {
            logger.info("Unlocked {} accounts that passed their lockout period", lockedUsers.size());
        }
    }
}
