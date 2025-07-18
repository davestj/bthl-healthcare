/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/UserRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: User repository interface for comprehensive user data access operations
 * Description: I designed this repository interface to provide all necessary database operations
 *              for user management including authentication, authorization, and advanced querying.
 *              I've included custom queries for security and business logic requirements.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of UserRepository with comprehensive user data access methods
 * 
 * Git Commit: git commit -m "feat: add UserRepository interface with comprehensive user data access methods"
 * 
 * Next Dev Feature: Add user analytics queries and advanced search capabilities
 * TODO: Implement user preference queries and activity tracking methods
 */

package com.bthl.healthcare.repository;

import com.bthl.healthcare.model.User;
import com.bthl.healthcare.model.enums.UserStatus;
import com.bthl.healthcare.model.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * I created this UserRepository interface to handle all database operations
 * related to user management. I've included comprehensive query methods for
 * authentication, authorization, and business logic requirements.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // I implement basic user lookup methods for authentication

    /**
     * I find a user by their username for authentication purposes.
     * This method is case-sensitive to ensure security.
     * 
     * @param username the exact username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * I find a user by their email address for login and password reset.
     * This method is case-insensitive for better user experience.
     * 
     * @param email the email address to search for (case-insensitive)
     * @return Optional containing the user if found
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * I check if a username already exists in the system.
     * This method helps prevent duplicate usernames during registration.
     * 
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * I check if an email address is already registered.
     * This method helps prevent duplicate email addresses during registration.
     * 
     * @param email the email address to check
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    // I implement user status and type filtering methods

    /**
     * I find all users with a specific status for administrative management.
     * 
     * @param status the user status to filter by
     * @param pageable pagination information
     * @return Page of users with the specified status
     */
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    /**
     * I find all users of a specific type for role-based management.
     * 
     * @param userType the user type to filter by
     * @param pageable pagination information
     * @return Page of users with the specified type
     */
    Page<User> findByUserType(UserType userType, Pageable pageable);

    /**
     * I find users by both status and type for advanced filtering.
     * 
     * @param status the user status to filter by
     * @param userType the user type to filter by
     * @param pageable pagination information
     * @return Page of users matching both criteria
     */
    Page<User> findByStatusAndUserType(UserStatus status, UserType userType, Pageable pageable);

    // I implement security-related query methods

    /**
     * I find users with failed login attempts above a threshold.
     * This method helps identify potential security threats or locked accounts.
     * 
     * @param threshold the minimum number of failed attempts
     * @return List of users with high failed login attempts
     */
    @Query("SELECT u FROM User u WHERE u.failedLoginAttempts >= :threshold")
    List<User> findUsersWithHighFailedLoginAttempts(@Param("threshold") int threshold);

    /**
     * I find users whose accounts are currently locked.
     * This method helps administrators manage locked accounts.
     * 
     * @param currentTime the current timestamp to compare against
     * @return List of currently locked users
     */
    @Query("SELECT u FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > :currentTime")
    List<User> findLockedUsers(@Param("currentTime") LocalDateTime currentTime);

    /**
     * I find users with MFA enabled for security reporting.
     * 
     * @return List of users who have enabled multi-factor authentication
     */
    List<User> findByMfaEnabledTrue();

    /**
     * I find users with unverified email addresses.
     * This method helps identify users who haven't completed registration.
     * 
     * @return List of users with unverified emails
     */
    List<User> findByEmailVerifiedFalse();

    // I implement password reset functionality methods

    /**
     * I find a user by their password reset token for password reset validation.
     * 
     * @param token the password reset token
     * @return Optional containing the user if token is valid
     */
    Optional<User> findByPasswordResetToken(String token);

    /**
     * I find a user by their email verification token for email verification.
     * 
     * @param token the email verification token
     * @return Optional containing the user if token is valid
     */
    Optional<User> findByEmailVerificationToken(String token);

    // I implement user activity and analytics methods

    /**
     * I find users who haven't logged in recently for engagement analysis.
     * 
     * @param cutoffDate the date before which users are considered inactive
     * @param pageable pagination information
     * @return Page of users who haven't logged in since the cutoff date
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin IS NULL OR u.lastLogin < :cutoffDate")
    Page<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate, Pageable pageable);

    /**
     * I find recently created users for onboarding and analytics.
     * 
     * @param since the date since which to find new users
     * @return List of users created since the specified date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :since ORDER BY u.createdAt DESC")
    List<User> findRecentlyCreatedUsers(@Param("since") LocalDateTime since);

    /**
     * I count users by their current status for dashboard metrics.
     * 
     * @param status the status to count
     * @return the number of users with the specified status
     */
    long countByStatus(UserStatus status);

    /**
     * I count users by their type for reporting purposes.
     * 
     * @param userType the user type to count
     * @return the number of users with the specified type
     */
    long countByUserType(UserType userType);

    // I implement search and filtering methods

    /**
     * I search users by name or email for administrative purposes.
     * This method provides flexible search capabilities for user management.
     * 
     * @param searchTerm the term to search for in names and email
     * @param pageable pagination information
     * @return Page of users matching the search criteria
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * I find users by role for role-based management.
     * 
     * @param roleId the UUID of the role to filter by
     * @param pageable pagination information
     * @return Page of users with the specified role
     */
    @Query("SELECT u FROM User u WHERE u.role.id = :roleId")
    Page<User> findByRoleId(@Param("roleId") UUID roleId, Pageable pageable);

    // I implement bulk update operations for administrative efficiency

    /**
     * I update the last login time for a user after successful authentication.
     * This method optimizes performance by avoiding full entity updates.
     * 
     * @param userId the UUID of the user
     * @param lastLogin the timestamp of the last login
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin, u.failedLoginAttempts = 0 WHERE u.id = :userId")
    int updateLastLogin(@Param("userId") UUID userId, @Param("lastLogin") LocalDateTime lastLogin);

    /**
     * I increment the failed login attempts counter for security tracking.
     * 
     * @param userId the UUID of the user
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 WHERE u.id = :userId")
    int incrementFailedLoginAttempts(@Param("userId") UUID userId);

    /**
     * I lock a user account for security purposes.
     * 
     * @param userId the UUID of the user to lock
     * @param lockUntil the timestamp until which the account should be locked
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE User u SET u.accountLockedUntil = :lockUntil WHERE u.id = :userId")
    int lockUserAccount(@Param("userId") UUID userId, @Param("lockUntil") LocalDateTime lockUntil);

    /**
     * I unlock a user account and reset failed login attempts.
     * 
     * @param userId the UUID of the user to unlock
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE User u SET u.accountLockedUntil = NULL, u.failedLoginAttempts = 0 WHERE u.id = :userId")
    int unlockUserAccount(@Param("userId") UUID userId);

    /**
     * I verify a user's email address by clearing the verification token.
     * 
     * @param userId the UUID of the user
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE User u SET u.emailVerified = true, u.emailVerificationToken = NULL WHERE u.id = :userId")
    int verifyUserEmail(@Param("userId") UUID userId);

    /**
     * I update a user's status for administrative management.
     * 
     * @param userId the UUID of the user
     * @param status the new status to set
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :userId")
    int updateUserStatus(@Param("userId") UUID userId, @Param("status") UserStatus status);

    // I implement specialized queries for business logic

    /**
     * I find users who need password reset token cleanup.
     * This method helps maintain database cleanliness by finding expired tokens.
     * 
     * @param cutoffTime the time before which tokens are considered expired
     * @return List of users with expired password reset tokens
     */
    @Query("SELECT u FROM User u WHERE u.passwordResetToken IS NOT NULL AND u.passwordResetExpires < :cutoffTime")
    List<User> findUsersWithExpiredPasswordResetTokens(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * I clean up expired password reset tokens for security.
     * 
     * @param cutoffTime the time before which tokens should be cleared
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE User u SET u.passwordResetToken = NULL, u.passwordResetExpires = NULL " +
           "WHERE u.passwordResetToken IS NOT NULL AND u.passwordResetExpires < :cutoffTime")
    int clearExpiredPasswordResetTokens(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * I find users for administrative dashboard statistics.
     * This method provides comprehensive user metrics for reporting.
     * 
     * @return aggregated user statistics
     */
    @Query("SELECT " +
           "COUNT(u) as totalUsers, " +
           "SUM(CASE WHEN u.status = 'ACTIVE' THEN 1 ELSE 0 END) as activeUsers, " +
           "SUM(CASE WHEN u.mfaEnabled = true THEN 1 ELSE 0 END) as mfaUsers, " +
           "SUM(CASE WHEN u.emailVerified = true THEN 1 ELSE 0 END) as verifiedUsers " +
           "FROM User u")
    Object[] getUserStatistics();

    /**
     * I find users by creation date range for analytics.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return List of users created within the date range
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate ORDER BY u.createdAt DESC")
    List<User> findUsersByCreationDateRange(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    /**
     * I find active users for various business operations.
     * This method returns only users who can actively use the system.
     * 
     * @param pageable pagination information
     * @return Page of active and verified users
     */
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.emailVerified = true")
    Page<User> findActiveUsers(Pageable pageable);
}
