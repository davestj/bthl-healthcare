/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/RoleRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-17
 * Purpose: Role repository interface for role-based access control data operations
 * Description: I designed this repository to provide comprehensive data access for role management
 *              including role hierarchy, permission management, and user assignment operations.
 *              I corrected this file to contain only the RoleRepository interface following
 *              Java's one-class-per-file organizational principle.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of RoleRepository with comprehensive role data access methods
 * 2025-07-17: Fixed file structure to contain only RoleRepository interface
 * 
 * Git Commit: git commit -m "fix: separate RoleRepository into individual file following Java conventions"
 * 
 * Next Dev Feature: Add role hierarchy queries and permission inheritance methods
 * TODO: Implement role template and bulk assignment capabilities
 */

package com.bthl.healthcare.repository;

import com.bthl.healthcare.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * I created this RoleRepository interface to handle all database operations
 * related to role management and RBAC functionality. This interface extends
 * JpaRepository to provide standard CRUD operations plus custom query methods
 * for role-specific business logic.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * I find a role by its unique name for role assignment operations.
     * This method is essential for looking up roles during user registration
     * and permission checking processes.
     * 
     * @param name the role name to search for
     * @return Optional containing the role if found, empty otherwise
     */
    Optional<Role> findByName(String name);

    /**
     * I check if a role name already exists to prevent duplicates.
     * This validation method ensures role name uniqueness across the system.
     * 
     * @param name the role name to check
     * @return true if a role with this name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * I find all system roles for administrative purposes.
     * System roles are predefined roles that cannot be deleted or modified
     * by regular administrators, ensuring system security and stability.
     * 
     * @return List of all system roles
     */
    List<Role> findByIsSystemRoleTrue();

    /**
     * I find all custom (non-system) roles for management.
     * These are user-created roles that can be modified or deleted
     * as needed for organizational requirements.
     * 
     * @return List of all custom roles
     */
    List<Role> findByIsSystemRoleFalse();

    /**
     * I search roles by name or description for administrative filtering.
     * This method provides flexible search capability for role management
     * interfaces, allowing administrators to quickly find specific roles.
     * 
     * @param searchTerm the term to search for in role names and descriptions
     * @param pageable pagination information
     * @return Page of roles matching the search criteria
     */
    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Role> searchRoles(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * I find roles that contain a specific permission.
     * This method is crucial for permission auditing and security analysis,
     * allowing administrators to see which roles grant specific permissions.
     * 
     * @param permission the permission to search for
     * @return List of roles that include the specified permission
     */
    @Query("SELECT r FROM Role r WHERE :permission MEMBER OF r.permissions")
    List<Role> findByPermission(@Param("permission") String permission);

    /**
     * I count the number of users assigned to each role.
     * This statistical method provides valuable insights for role management
     * and helps administrators understand role usage patterns.
     * 
     * @return List of Object arrays containing role ID, name, and user count
     */
    @Query("SELECT r.id, r.name, COUNT(u) FROM Role r LEFT JOIN r.users u GROUP BY r.id, r.name")
    List<Object[]> getRoleUserCounts();

    /**
     * I find roles that can be safely deleted (no users assigned).
     * This method helps administrators identify unused custom roles
     * that can be cleaned up without affecting any users.
     * 
     * @return List of deletable roles (custom roles with no assigned users)
     */
    @Query("SELECT r FROM Role r WHERE r.isSystemRole = false AND SIZE(r.users) = 0")
    List<Role> findDeletableRoles();
}
