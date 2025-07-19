package com.bthl.healthcare.repository;

import com.bthl.healthcare.model.AuditLog;
import com.bthl.healthcare.model.User;
import com.bthl.healthcare.model.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/AuditLogRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Repository interface for audit log data access in BTHL-HealthCare platform
 * Git Commit: git commit -m "feat: add AuditLogRepository interface for audit trail data access"
 * Next Dev Feature: Add custom query methods for audit log analytics and reporting
 */

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    
    List<AuditLog> findByUserAndTimestampBetweenOrderByTimestampDesc(User user, LocalDateTime start, LocalDateTime end);
    
    List<AuditLog> findByUserAndTimestampBetweenAndActionOrderByTimestampDesc(User user, LocalDateTime start, LocalDateTime end, AuditAction action);
    
    List<AuditLog> findByResourceTypeAndTimestampBetweenOrderByTimestampDesc(String resourceType, LocalDateTime start, LocalDateTime end);
    
    long countByResourceNameAndActionAndTimestampAfterAndDetailsContaining(String resourceName, AuditAction action, LocalDateTime timestamp, String details);
    
    long countByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    long countByActionAndTimestampBetween(AuditAction action, LocalDateTime start, LocalDateTime end);
    
    long countByResourceTypeAndTimestampBetween(String resourceType, LocalDateTime start, LocalDateTime end);
}
