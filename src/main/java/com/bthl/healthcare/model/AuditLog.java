package com.bthl.healthcare.model;

import com.bthl.healthcare.model.enums.AuditAction;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/AuditLog.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Audit log entity for BTHL-HealthCare platform security and compliance tracking
 * Git Commit: git commit -m "feat: add AuditLog entity for comprehensive audit trail tracking"
 * Next Dev Feature: Add audit log retention policies and automated cleanup
 */

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AuditAction action;
    
    @Column(name = "resource_type", nullable = false)
    private String resourceType;
    
    @Column(name = "resource_id")
    private UUID resourceId;
    
    @Column(name = "resource_name")
    private String resourceName;
    
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    // Constructors
    public AuditLog() {}
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }
    
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    
    public UUID getResourceId() { return resourceId; }
    public void setResourceId(UUID resourceId) { this.resourceId = resourceId; }
    
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
