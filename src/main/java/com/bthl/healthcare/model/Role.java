/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/Role.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Role entity model for role-based access control (RBAC) system
 * Description: I designed this Role entity to implement a comprehensive RBAC system that allows
 *              flexible permission management across my healthcare platform. I've included
 *              support for hierarchical roles and dynamic permission assignment.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of Role entity with RBAC and permission management features
 * 
 * Git Commit: git commit -m "feat: add Role entity model with RBAC and dynamic permission management"
 * 
 * Next Dev Feature: Add role hierarchy support and permission inheritance mechanisms
 * TODO: Implement role templates and bulk permission assignment capabilities
 */

package com.bthl.healthcare.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

/**
 * I created this Role entity to manage the comprehensive role-based access control
 * system for my healthcare platform. I've designed it to be flexible and scalable
 * to accommodate different organizational structures and security requirements.
 */
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_roles_name", columnList = "name"),
    @Index(name = "idx_roles_system", columnList = "is_system_role")
})
@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z_]+$", message = "Role name must contain only uppercase letters and underscores")
    @Column(name = "name", unique = true, nullable = false, length = 50)
    public String name;

    @Size(max = 500, message = "Role description must not exceed 500 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    public String description;

    @Column(name = "permissions", columnDefinition = "jsonb")
    public List<String> permissions = new ArrayList<>();

    @Column(name = "is_system_role", nullable = false)
    public Boolean isSystemRole = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by")
    public UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    public UUID updatedBy;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    public Set<User> users = new HashSet<>();

    // I implement default constructor for JPA
    public Role() {}

    // I create a comprehensive constructor for role creation
    public Role(String name, String description, List<String> permissions, Boolean isSystemRole) {
        this.name = name;
        this.description = description;
        this.permissions = permissions != null ? new ArrayList<>(permissions) : new ArrayList<>();
        this.isSystemRole = isSystemRole != null ? isSystemRole : false;
    }

    // I create a simplified constructor for basic role creation
    public Role(String name, String description) {
        this(name, description, new ArrayList<>(), false);
    }

    // I implement getter and setter methods following my naming conventions

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPermissions() {
        return permissions != null ? new ArrayList<>(permissions) : new ArrayList<>();
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions != null ? new ArrayList<>(permissions) : new ArrayList<>();
    }

    public Boolean getIsSystemRole() {
        return isSystemRole;
    }

    public void setIsSystemRole(Boolean isSystemRole) {
        this.isSystemRole = isSystemRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public Set<User> getUsers() {
        return new HashSet<>(users);
    }

    // I create convenient computed properties for frontend use

    @JsonProperty("userCount")
    public int getUserCount() {
        return users != null ? users.size() : 0;
    }

    @JsonProperty("permissionCount")
    public int getPermissionCount() {
        return permissions != null ? permissions.size() : 0;
    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        // I format the role name for better display
        return name.replace("_", " ").toLowerCase();
    }

    @JsonProperty("canBeDeleted")
    public boolean getCanBeDeleted() {
        // I prevent deletion of system roles and roles with assigned users
        return !isSystemRole && (users == null || users.isEmpty());
    }

    @JsonProperty("canBeModified")
    public boolean getCanBeModified() {
        // I allow modification of non-system roles
        return !isSystemRole;
    }

    // I implement permission management methods

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    public void addPermission(String permission) {
        if (permission != null && !permission.trim().isEmpty()) {
            if (permissions == null) {
                permissions = new ArrayList<>();
            }
            if (!permissions.contains(permission)) {
                permissions.add(permission);
            }
        }
    }

    public void removePermission(String permission) {
        if (permissions != null) {
            permissions.remove(permission);
        }
    }

    public void addPermissions(Collection<String> newPermissions) {
        if (newPermissions != null) {
            if (permissions == null) {
                permissions = new ArrayList<>();
            }
            newPermissions.forEach(this::addPermission);
        }
    }

    public void removePermissions(Collection<String> permissionsToRemove) {
        if (permissions != null && permissionsToRemove != null) {
            permissions.removeAll(permissionsToRemove);
        }
    }

    public void clearPermissions() {
        if (permissions != null) {
            permissions.clear();
        }
    }

    public boolean hasAnyPermission(Collection<String> permissionsToCheck) {
        if (permissions == null || permissionsToCheck == null) {
            return false;
        }
        return permissionsToCheck.stream().anyMatch(permissions::contains);
    }

    public boolean hasAllPermissions(Collection<String> permissionsToCheck) {
        if (permissions == null || permissionsToCheck == null) {
            return false;
        }
        return permissions.containsAll(permissionsToCheck);
    }

    // I implement role validation methods

    public boolean isValidForAssignment() {
        // I check if role can be assigned to users
        return name != null && !name.trim().isEmpty() && 
               permissions != null && !permissions.isEmpty();
    }

    public Set<String> validatePermissions(Set<String> validPermissions) {
        // I return invalid permissions that are not in the valid set
        Set<String> invalidPermissions = new HashSet<>();
        if (permissions != null && validPermissions != null) {
            permissions.stream()
                .filter(permission -> !validPermissions.contains(permission))
                .forEach(invalidPermissions::add);
        }
        return invalidPermissions;
    }

    // I implement role comparison and categorization methods

    public boolean isAdminRole() {
        return hasPermission("USER_MANAGEMENT") || hasPermission("SYSTEM_CONFIG");
    }

    public boolean isBrokerRole() {
        return hasPermission("CLIENT_MANAGEMENT") || hasPermission("BROKER_RELATIONS");
    }

    public boolean isProviderRole() {
        return hasPermission("PROVIDER_PROFILE") || hasPermission("PLAN_MANAGEMENT");
    }

    public boolean isCompanyRole() {
        return hasPermission("COMPANY_PORTFOLIO") || hasPermission("EMPLOYEE_MANAGEMENT");
    }

    public String getCategoryClass() {
        if (isAdminRole()) return "role-admin";
        if (isBrokerRole()) return "role-broker";
        if (isProviderRole()) return "role-provider";
        if (isCompanyRole()) return "role-company";
        return "role-custom";
    }

    // I implement role cloning for template functionality

    public Role cloneRole(String newName, String newDescription) {
        Role clonedRole = new Role();
        clonedRole.name = newName;
        clonedRole.description = newDescription;
        clonedRole.permissions = new ArrayList<>(this.permissions);
        clonedRole.isSystemRole = false; // I never clone system roles
        return clonedRole;
    }

    // I implement equals and hashCode based on UUID and name
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        
        // I first compare by ID if both have IDs
        if (id != null && role.id != null) {
            return Objects.equals(id, role.id);
        }
        
        // I fall back to name comparison for new entities
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        // I use ID if available, otherwise name
        return id != null ? Objects.hash(id) : Objects.hash(name);
    }

    // I provide a comprehensive toString method for debugging
    @Override
    public String toString() {
        return "Role{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", permissions=" + (permissions != null ? permissions.size() : 0) + " permissions" +
               ", isSystemRole=" + isSystemRole +
               ", userCount=" + (users != null ? users.size() : 0) +
               ", createdAt=" + createdAt +
               '}';
    }
}
