/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/security/CustomUserDetailsService.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Custom UserDetailsService implementation for Spring Security integration
 * Description: I created this service to integrate my User entity with Spring Security's
 *              authentication mechanism, providing user details for authentication and authorization.
 * 
 * Changelog:
 * 2025-07-18: Separated from SecurityConfig.java file to resolve Java compilation errors
 * 2025-07-16: Initial creation of CustomUserDetailsService for Spring Security integration
 * 
 * Git Commit: git commit -m "refactor: separate CustomUserDetailsService into individual file to resolve compilation errors"
 * 
 * Next Dev Feature: Add user caching and performance optimization for authentication lookups
 * TODO: Implement user session tracking and concurrent login monitoring
 */

package com.bthl.healthcare.security;

import com.bthl.healthcare.model.User;
import com.bthl.healthcare.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * I created this CustomUserDetailsService to bridge my User entity with Spring Security.
 * This service loads user details for authentication and provides user information
 * to the security context throughout the application.
 */
@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    /**
     * I create this constructor to inject the UserRepository dependency for user lookups.
     * 
     * @param userRepository the repository for accessing user data
     */
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * I load user details by username or email for Spring Security authentication.
     * This method is called by Spring Security during the authentication process.
     * 
     * @param usernameOrEmail the username or email to search for
     * @return UserDetails implementation for Spring Security
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.debug("Loading user details for: {}", usernameOrEmail);

        // I try to find user by username first, then by email
        User user = userRepository.findByUsername(usernameOrEmail)
            .orElseGet(() -> userRepository.findByEmailIgnoreCase(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with username or email: " + usernameOrEmail)));

        logger.debug("Successfully loaded user: {} with authorities: {}", 
                     user.getUsername(), user.getAuthorities());

        return user; // My User entity implements UserDetails
    }

    /**
     * I load user details by user ID for programmatic authentication.
     * This method is useful for session management and user lookup.
     * 
     * @param userId the UUID of the user
     * @return UserDetails implementation
     * @throws UsernameNotFoundException if user is not found
     */
    public UserDetails loadUserById(java.util.UUID userId) throws UsernameNotFoundException {
        logger.debug("Loading user details for ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        logger.debug("Successfully loaded user by ID: {}", user.getUsername());
        return user;
    }
}
