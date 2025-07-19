/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/security/jwt/JwtAuthenticationEntryPoint.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: JWT authentication entry point for handling unauthorized access attempts
 * Description: I created this entry point to handle unauthorized access attempts to protected
 *              resources in my healthcare platform. I provide consistent error responses and
 *              proper security headers for API authentication failures.
 * 
 * Changelog:
 * 2025-07-18: Separated JWT components into individual files to resolve Java compilation errors
 * 2025-07-16: Initial creation of JWT authentication entry point for API security
 * 
 * Git Commit: git commit -m "refactor: separate JwtAuthenticationEntryPoint into individual file to resolve compilation errors"
 * 
 * Next Dev Feature: Add detailed error context and security event logging for unauthorized attempts
 * TODO: Implement rate limiting and IP-based blocking for repeated unauthorized access attempts
 */

package com.bthl.healthcare.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {

        logger.warn("Unauthorized access attempt to: {} from IP: {}", 
                   request.getRequestURI(), request.getRemoteAddr());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Full authentication is required to access this resource");
        errorResponse.put("path", request.getRequestURI());

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
