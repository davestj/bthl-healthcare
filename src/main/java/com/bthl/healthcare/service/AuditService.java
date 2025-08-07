package com.bthl.healthcare.service;

import com.bthl.healthcare.model.AuditLog;
import com.bthl.healthcare.model.User;
import com.bthl.healthcare.model.enums.AuditAction;
import com.bthl.healthcare.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/service/AuditService.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Audit service for BTHL-HealthCare platform security and compliance logging
 * Git Commit: git commit -m "feat: add AuditService basic implementation for compilation resolution"
 * Next Dev Feature: Add comprehensive audit logging with database persistence
 */

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * I log generic user actions to the audit log repository.
     */
    public void logUserAction(User user, AuditAction action, String resourceType,
                             UUID resourceId, String resourceName, String details,
                             HttpServletRequest request) {
        logger.info("Logging user action: {} by user: {} on resource: {}",
                    action, user != null ? user.getUsername() : "anonymous", resourceType);
        saveAuditLog(user, action, resourceType, resourceId, resourceName, details);
    }

    /**
     * I log security related events such as account lockouts.
     */
    public void logSecurityEvent(String username, AuditAction action, String details,
                                HttpServletRequest request) {
        logger.warn("Logging security event: {} for user: {}", action, username);
        saveAuditLog(null, action, "SECURITY", null, username, details);
    }

    /**
     * I log authentication events including successes and failures.
     */
    public void logAuthenticationEvent(String username, AuditAction action, boolean successful,
                                     String failureReason, HttpServletRequest request) {
        logger.info("Logging authentication event: {} for user: {} success: {}",
                    action, username, successful);
        String details = successful ? "Authentication successful" : "Authentication failed: " + failureReason;
        saveAuditLog(null, action, "AUTH", null, username, details);
    }

    /**
     * I log when a user creates their own account.
     */
    public void logUserCreation(User user) {
        saveAuditLog(user, AuditAction.CREATE, "USER", user.getId(), user.getUsername(),
                "User self-registration");
    }

    /**
     * I log when an administrator creates a user account.
     */
    public void logUserCreationByAdmin(User user, User admin) {
        saveAuditLog(admin, AuditAction.CREATE, "USER", user.getId(), user.getUsername(),
                "Administrator " + admin.getUsername() + " created user " + user.getUsername());
    }

    /**
     * I log a failed login attempt with a reason.
     */
    public void logFailedLoginAttempt(User user, String reason) {
        saveAuditLog(user, AuditAction.FAILED_LOGIN, "AUTH", user.getId(), user.getUsername(),
                "Failed login: " + reason);
    }

    /**
     * I log a successful login for a user.
     */
    public void logSuccessfulLogin(User user) {
        saveAuditLog(user, AuditAction.LOGIN, "AUTH", user.getId(), user.getUsername(),
                "Successful login");
    }

    /**
     * I log an attempt to change a user's password.
     */
    public void logPasswordChangeAttempt(User user, boolean success, String reason) {
        String detail = success ? "Password change attempt successful"
                : "Password change attempt failed: " + reason;
        saveAuditLog(user, AuditAction.UPDATE, "AUTH", user.getId(), user.getUsername(), detail);
    }

    /**
     * I log when a user changes their password successfully.
     */
    public void logPasswordChange(User user) {
        saveAuditLog(user, AuditAction.UPDATE, "AUTH", user.getId(), user.getUsername(),
                "Password changed");
    }

    /**
     * I log when a user requests a password reset.
     */
    public void logPasswordResetRequest(User user) {
        saveAuditLog(user, AuditAction.UPDATE, "AUTH", user.getId(), user.getUsername(),
                "Password reset requested");
    }

    /**
     * I log when a user resets their password.
     */
    public void logPasswordReset(User user) {
        saveAuditLog(user, AuditAction.UPDATE, "AUTH", user.getId(), user.getUsername(),
                "Password reset completed");
    }

    /**
     * I log when a user enables MFA.
     */
    public void logMfaEnabled(User user) {
        saveAuditLog(user, AuditAction.UPDATE, "AUTH", user.getId(), user.getUsername(),
                "MFA enabled");
    }

    /**
     * I log when a user disables MFA.
     */
    public void logMfaDisabled(User user) {
        saveAuditLog(user, AuditAction.UPDATE, "AUTH", user.getId(), user.getUsername(),
                "MFA disabled");
    }

    /**
     * I log updates to a user's profile.
     */
    public void logUserProfileUpdate(User user) {
        saveAuditLog(user, AuditAction.UPDATE, "USER", user.getId(), user.getUsername(),
                "User profile updated");
    }

    /**
     * I log activation of a user account by an administrator.
     */
    public void logUserActivation(User user, User activatedBy) {
        saveAuditLog(activatedBy, AuditAction.UPDATE, "USER", user.getId(), user.getUsername(),
                "User account activated");
    }

    /**
     * I log deactivation of a user account by an administrator.
     */
    public void logUserDeactivation(User user, User deactivatedBy) {
        saveAuditLog(deactivatedBy, AuditAction.UPDATE, "USER", user.getId(), user.getUsername(),
                "User account deactivated");
    }

    /**
     * I log when an account is unlocked after a lockout period.
     */
    public void logAccountUnlock(User user) {
        saveAuditLog(user, AuditAction.UPDATE, "USER", user.getId(), user.getUsername(),
                "Account unlocked");
    }

    /**
     * I build and persist the audit log entry with request metadata.
     */
    private void saveAuditLog(User actor, AuditAction action, String resourceType,
                              UUID resourceId, String resourceName, String details) {
        HttpServletRequest request = getCurrentRequest();

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(actor);
        auditLog.setAction(action);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setResourceName(resourceName);
        auditLog.setDetails(details);

        if (request != null) {
            auditLog.setIpAddress(request.getRemoteAddr());
            auditLog.setUserAgent(request.getHeader("User-Agent"));
            if (request.getSession(false) != null) {
                auditLog.setSessionId(request.getSession(false).getId());
            }
        }

        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    /**
     * I obtain the current HTTP request if available.
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}
