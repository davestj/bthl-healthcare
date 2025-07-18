/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/security/jwt/JwtAuthenticationEntryPoint.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: JWT authentication entry point for handling unauthorized requests
 * Description: I designed this entry point to handle authentication failures and return
 *              appropriate HTTP responses for both web and API requests.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of JWT authentication entry point
 * 
 * Git Commit: git commit -m "feat: add JWT authentication entry point for API security"
 * 
 * Next Dev Feature: Add detailed error response formatting and logging
 * TODO: Implement rate limiting for failed authentication attempts
 */

package com.bthl.healthcare.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * I created this JwtAuthenticationEntryPoint to handle authentication failures
 * and provide appropriate responses for both API and web requests.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    public final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * I handle authentication failures by returning appropriate responses
     * based on whether the request is for API or web resources.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        String requestUri = request.getRequestURI();
        logger.warn("Authentication failed for request: {} - {}", requestUri, authException.getMessage());

        // I check if this is an API request
        if (requestUri.startsWith("/api/")) {
            handleApiAuthenticationFailure(request, response, authException);
        } else {
            handleWebAuthenticationFailure(request, response, authException);
        }
    }

    /**
     * I handle API authentication failures with JSON error responses.
     */
    public void handleApiAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", 401);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Authentication required");
        errorResponse.put("path", request.getRequestURI());

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

    /**
     * I handle web authentication failures by redirecting to login page.
     */
    public void handleWebAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException authException) throws IOException {
        response.sendRedirect("/login?error=unauthorized");
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/security/jwt/JwtAuthenticationFilter.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: JWT authentication filter for processing JWT tokens in requests
 * Description: I created this filter to extract and validate JWT tokens from requests,
 *              setting up the security context for authenticated users.
 */

package com.bthl.healthcare.security.jwt;

import com.bthl.healthcare.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * I created this JwtAuthenticationFilter to process JWT tokens in incoming requests
 * and establish the security context for authenticated users.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public final JwtTokenProvider jwtTokenProvider;
    public final CustomUserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, 
                                  CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * I filter incoming requests to extract and validate JWT tokens.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Set authentication for user: {}", username);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * I extract the JWT token from the Authorization header.
     */
    public String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/security/jwt/JwtTokenProvider.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: JWT token provider for creating and validating JWT tokens
 * Description: I designed this provider to handle JWT token creation, validation, and extraction
 *              of user information for API authentication in my healthcare platform.
 */

package com.bthl.healthcare.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * I created this JwtTokenProvider to handle all JWT token operations including
 * creation, validation, and information extraction for API authentication.
 */
@Component
public class JwtTokenProvider {

    public static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${bthl.healthcare.security.jwt.secret}")
    public String jwtSecret;

    @Value("${bthl.healthcare.security.jwt.expiration}")
    public long jwtExpirationInMs;

    @Value("${bthl.healthcare.security.jwt.refresh-expiration}")
    public long refreshExpirationInMs;

    public Algorithm algorithm;
    public JWTVerifier verifier;

    /**
     * I initialize the JWT algorithm and verifier after properties are set.
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC512(jwtSecret);
        this.verifier = JWT.require(algorithm)
            .withIssuer("bthl-healthcare")
            .build();
    }

    /**
     * I create a JWT token from an authenticated user.
     */
    public String createToken(Authentication authentication) {
        com.bthl.healthcare.model.User userPrincipal = 
            (com.bthl.healthcare.model.User) authentication.getPrincipal();

        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

        String authorities = userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        return JWT.create()
            .withIssuer("bthl-healthcare")
            .withSubject(userPrincipal.getUsername())
            .withClaim("userId", userPrincipal.getId().toString())
            .withClaim("email", userPrincipal.getEmail())
            .withClaim("authorities", authorities)
            .withClaim("userType", userPrincipal.getUserType().name())
            .withIssuedAt(new Date())
            .withExpiresAt(expiryDate)
            .sign(algorithm);
    }

    /**
     * I create a refresh token for extended authentication.
     */
    public String createRefreshToken(Authentication authentication) {
        com.bthl.healthcare.model.User userPrincipal = 
            (com.bthl.healthcare.model.User) authentication.getPrincipal();

        Date expiryDate = new Date(System.currentTimeMillis() + refreshExpirationInMs);

        return JWT.create()
            .withIssuer("bthl-healthcare")
            .withSubject(userPrincipal.getUsername())
            .withClaim("userId", userPrincipal.getId().toString())
            .withClaim("tokenType", "refresh")
            .withIssuedAt(new Date())
            .withExpiresAt(expiryDate)
            .sign(algorithm);
    }

    /**
     * I extract the username from a JWT token.
     */
    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }

    /**
     * I extract the user ID from a JWT token.
     */
    public String getUserIdFromToken(String token) {
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("userId").asString();
    }

    /**
     * I extract the user email from a JWT token.
     */
    public String getEmailFromToken(String token) {
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("email").asString();
    }

    /**
     * I extract the authorities from a JWT token.
     */
    public String getAuthoritiesFromToken(String token) {
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("authorities").asString();
    }

    /**
     * I validate a JWT token for authenticity and expiration.
     */
    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            logger.error("JWT token validation failed: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * I check if a JWT token is expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (JWTVerificationException ex) {
            return true;
        }
    }

    /**
     * I get the expiration date from a JWT token.
     */
    public Date getExpirationDateFromToken(String token) {
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getExpiresAt();
    }

    /**
     * I refresh an existing JWT token if it's valid.
     */
    public String refreshToken(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Invalid token for refresh");
        }

        String username = getUsernameFromToken(token);
        String userId = getUserIdFromToken(token);
        String email = getEmailFromToken(token);
        String authorities = getAuthoritiesFromToken(token);

        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

        return JWT.create()
            .withIssuer("bthl-healthcare")
            .withSubject(username)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withClaim("authorities", authorities)
            .withIssuedAt(new Date())
            .withExpiresAt(expiryDate)
            .sign(algorithm);
    }
}
