/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/security/jwt/JwtTokenProvider.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: JWT token provider for creating and validating JWT tokens
 * Description: I designed this provider to handle JWT token creation, validation, and extraction
 *              of user information for API authentication in my healthcare platform. I use the
 *              Auth0 JWT library for robust token management and security.
 * 
 * Changelog:
 * 2025-07-18: Separated from JwtAuthenticationEntryPoint.java file to resolve Java compilation errors
 * 2025-07-16: Initial creation of JWT token provider for API authentication
 * 
 * Git Commit: git commit -m "refactor: separate JwtTokenProvider into individual file to resolve compilation errors"
 * 
 * Next Dev Feature: Add token rotation and enhanced security claims
 * TODO: Implement token blacklisting and advanced token analytics
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

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${bthl.healthcare.jwt.secret}")
    private String jwtSecret;

    @Value("${bthl.healthcare.jwt.expiration}")
    private int jwtExpirationInMs;

    public String createToken(Authentication authentication) {
        String userId = ((com.bthl.healthcare.model.User) authentication.getPrincipal()).getId().toString();
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return JWT.create()
                .withSubject(userId)
                .withClaim("username", username)
                .withClaim("authorities", authorities)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withIssuer("bthl-healthcare")
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public String createRefreshToken(Authentication authentication) {
        String userId = ((com.bthl.healthcare.model.User) authentication.getPrincipal()).getId().toString();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (jwtExpirationInMs * 7));

        return JWT.create()
                .withSubject(userId)
                .withClaim("type", "refresh")
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withIssuer("bthl-healthcare")
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret))
                    .withIssuer("bthl-healthcare")
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            logger.error("Invalid JWT token: {}", exception.getMessage());
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("username").asString();
    }

    public String getAuthoritiesFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("authorities").asString();
    }

    public String refreshToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String userId = getUserIdFromToken(refreshToken);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withIssuer("bthl-healthcare")
                .sign(Algorithm.HMAC512(jwtSecret));
    }
}
