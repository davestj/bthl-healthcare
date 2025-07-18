/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/security/SecurityConfig.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Spring Security configuration for comprehensive authentication and authorization
 * Description: I designed this security configuration to provide robust authentication, authorization,
 *              CSRF protection, session management, and security headers for my healthcare platform.
 *              I've implemented both form-based and API-based authentication with proper security controls.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of Spring Security configuration with comprehensive security controls
 * 
 * Git Commit: git commit -m "feat: add comprehensive Spring Security configuration with authentication and authorization"
 * 
 * Next Dev Feature: Add OAuth2 integration and advanced security monitoring capabilities
 * TODO: Implement rate limiting and advanced threat detection mechanisms
 */

package com.bthl.healthcare.security;

import com.bthl.healthcare.service.UserService;
import com.bthl.healthcare.security.jwt.JwtAuthenticationEntryPoint;
import com.bthl.healthcare.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * I created this SecurityConfig class to configure comprehensive security for my
 * healthcare platform. I've implemented multiple layers of security including
 * authentication, authorization, CSRF protection, and security headers.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    public final UserDetailsService userDetailsService;
    public final PasswordEncoder passwordEncoder;
    public final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    public final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${bthl.healthcare.security.session.max-concurrent-sessions:3}")
    public int maxConcurrentSessions;

    /**
     * I create this constructor to inject all security-related dependencies.
     */
    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
                         JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * I configure the main security filter chain with comprehensive security controls.
     * This method defines all security rules, authentication mechanisms, and access controls.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // I configure CORS settings
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // I configure CSRF protection with exemptions for API endpoints
            .csrf(csrf -> csrf
                .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/**", "/auth/**", "/actuator/**")
            )
            
            // I configure session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(maxConcurrentSessions)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry())
                .and()
                .sessionFixation().changeSessionId()
                .invalidSessionUrl("/login?expired")
            )
            
            // I configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // I allow public access to static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                
                // I allow public access to authentication endpoints
                .requestMatchers("/", "/login", "/register", "/forgot-password", "/reset-password").permitAll()
                
                // I allow public access to API authentication endpoints
                .requestMatchers("/api/auth/**").permitAll()
                
                // I allow public access to health checks
                .requestMatchers("/actuator/health").permitAll()
                
                // I allow public access to error pages
                .requestMatchers("/error").permitAll()
                
                // I require ADMIN role for admin endpoints
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                
                // I require BROKER role for broker endpoints
                .requestMatchers("/broker/**", "/api/broker/**").hasRole("BROKER")
                
                // I require PROVIDER role for provider endpoints
                .requestMatchers("/provider/**", "/api/provider/**").hasRole("PROVIDER")
                
                // I require COMPANY_USER role for company endpoints
                .requestMatchers("/company/**", "/api/company/**").hasRole("COMPANY_USER")
                
                // I require authentication for all other requests
                .anyRequest().authenticated()
            )
            
            // I configure form-based login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            
            // I configure logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            )
            
            // I configure remember me functionality
            .rememberMe(remember -> remember
                .key("bthl-healthcare-remember-me")
                .tokenValiditySeconds(604800) // 7 days
                .userDetailsService(userDetailsService)
            )
            
            // I configure exception handling
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedPage("/access-denied")
            )
            
            // I configure security headers
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)
                    .preload(true)
                )
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .permissionsPolicy(permissions -> permissions
                    .policy("accelerometer", "camera", "geolocation", "gyroscope", 
                           "magnetometer", "microphone", "payment", "usb")
                )
            );

        // I add JWT authentication filter before username/password authentication
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * I configure the authentication provider with user details service and password encoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    /**
     * I provide the authentication manager for programmatic authentication.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * I configure CORS settings to allow frontend access while maintaining security.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // I allow specific origins for production security
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://davestj.com",
            "https://*.davestj.com",
            "http://localhost:*"
        ));
        
        // I allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // I allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers", "X-CSRF-TOKEN"
        ));
        
        // I expose specific headers to the frontend
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
            "Authorization", "X-CSRF-TOKEN"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/auth/**", configuration);
        
        return source;
    }

    /**
     * I configure session registry for session management and monitoring.
     */
    @Bean
    public org.springframework.security.core.session.SessionRegistry sessionRegistry() {
        return new org.springframework.security.core.session.SessionRegistryImpl();
    }

    /**
     * I configure session authentication strategy for concurrent session control.
     */
    @Bean
    public org.springframework.security.web.authentication.session.SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy strategy =
            new org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        strategy.setMaximumSessions(maxConcurrentSessions);
        strategy.setExceptionIfMaximumExceeded(false);
        return strategy;
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/security/CustomUserDetailsService.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Custom UserDetailsService implementation for Spring Security integration
 * Description: I created this service to integrate my User entity with Spring Security's
 *              authentication mechanism, providing user details for authentication and authorization.
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

    public static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public final UserRepository userRepository;

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
