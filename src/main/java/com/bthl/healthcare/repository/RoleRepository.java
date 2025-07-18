/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/RoleRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Role repository interface for role-based access control data operations
 * Description: I designed this repository to provide comprehensive data access for role management
 *              including role hierarchy, permission management, and user assignment operations.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of RoleRepository with comprehensive role data access methods
 * 
 * Git Commit: git commit -m "feat: add RoleRepository interface for RBAC data operations"
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
 * related to role management and RBAC functionality.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * I find a role by its unique name for role assignment operations.
     */
    Optional<Role> findByName(String name);

    /**
     * I check if a role name already exists to prevent duplicates.
     */
    boolean existsByName(String name);

    /**
     * I find all system roles for administrative purposes.
     */
    List<Role> findByIsSystemRoleTrue();

    /**
     * I find all custom (non-system) roles for management.
     */
    List<Role> findByIsSystemRoleFalse();

    /**
     * I search roles by name for administrative filtering.
     */
    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Role> searchRoles(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * I find roles that contain a specific permission.
     */
    @Query("SELECT r FROM Role r WHERE :permission MEMBER OF r.permissions")
    List<Role> findByPermission(@Param("permission") String permission);

    /**
     * I count the number of users assigned to each role.
     */
    @Query("SELECT r.id, r.name, COUNT(u) FROM Role r LEFT JOIN r.users u GROUP BY r.id, r.name")
    List<Object[]> getRoleUserCounts();

    /**
     * I find roles that can be safely deleted (no users assigned).
     */
    @Query("SELECT r FROM Role r WHERE r.isSystemRole = false AND SIZE(r.users) = 0")
    List<Role> findDeletableRoles();
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/CompanyRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Company repository interface for company management data operations
 */

package com.bthl.healthcare.repository;

import com.bthl.healthcare.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByName(String name);
    Optional<Company> findByTaxId(String taxId);
    boolean existsByName(String name);
    boolean existsByTaxId(String taxId);

    Page<Company> findByStatus(String status, Pageable pageable);
    Page<Company> findByIndustry(String industry, Pageable pageable);

    @Query("SELECT c FROM Company c WHERE c.employeeCount BETWEEN :minEmployees AND :maxEmployees")
    Page<Company> findByEmployeeCountRange(@Param("minEmployees") Integer minEmployees, 
                                          @Param("maxEmployees") Integer maxEmployees, 
                                          Pageable pageable);

    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.legalName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "c.taxId LIKE CONCAT('%', :searchTerm, '%')")
    Page<Company> searchCompanies(@Param("searchTerm") String searchTerm, Pageable pageable);

    List<Company> findTop10ByOrderByEmployeeCountDesc();
    
    @Query("SELECT COUNT(c) FROM Company c WHERE c.status = :status")
    long countByStatus(@Param("status") String status);
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/InsuranceProviderRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Insurance Provider repository interface for provider data operations
 */

package com.bthl.healthcare.repository;

import com.bthl.healthcare.model.InsuranceProvider;
import com.bthl.healthcare.model.enums.ProviderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsuranceProviderRepository extends JpaRepository<InsuranceProvider, UUID> {

    Optional<InsuranceProvider> findByProviderCode(String providerCode);
    boolean existsByProviderCode(String providerCode);
    boolean existsByName(String name);

    List<InsuranceProvider> findByProviderType(ProviderType providerType);
    Page<InsuranceProvider> findByProviderType(ProviderType providerType, Pageable pageable);
    Page<InsuranceProvider> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT p FROM InsuranceProvider p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.legalName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.providerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<InsuranceProvider> searchProviders(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT p FROM InsuranceProvider p WHERE :serviceArea MEMBER OF p.serviceAreas")
    List<InsuranceProvider> findByServiceArea(@Param("serviceArea") String serviceArea);

    List<InsuranceProvider> findTop10ByOrderByNetworkSizeDesc();
    
    long countByProviderType(ProviderType providerType);
    long countByIsActiveTrue();
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/InsuranceBrokerRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Insurance Broker repository interface for broker data operations
 */

package com.bthl.healthcare.repository;

import com.bthl.healthcare.model.InsuranceBroker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsuranceBrokerRepository extends JpaRepository<InsuranceBroker, UUID> {

    Optional<InsuranceBroker> findByUserId(UUID userId);
    Optional<InsuranceBroker> findByLicenseNumber(String licenseNumber);
    boolean existsByLicenseNumber(String licenseNumber);

    List<InsuranceBroker> findByLicenseState(String licenseState);
    Page<InsuranceBroker> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT b FROM InsuranceBroker b WHERE b.licenseExpires BETWEEN :startDate AND :endDate")
    List<InsuranceBroker> findByLicenseExpiringBetween(@Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM InsuranceBroker b WHERE :territory MEMBER OF b.territories")
    List<InsuranceBroker> findByTerritory(@Param("territory") String territory);

    @Query("SELECT b FROM InsuranceBroker b WHERE :specialization MEMBER OF b.specializations")
    List<InsuranceBroker> findBySpecialization(@Param("specialization") String specialization);

    @Query("SELECT b FROM InsuranceBroker b WHERE " +
           "LOWER(b.user.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.user.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.agencyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "b.licenseNumber LIKE CONCAT('%', :searchTerm, '%')")
    Page<InsuranceBroker> searchBrokers(@Param("searchTerm") String searchTerm, Pageable pageable);

    long countByIsActiveTrue();
    long countByLicenseState(String licenseState);
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/InsurancePlanRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Insurance Plan repository interface for plan data operations
 */

package com.bthl.healthcare.repository;

import com.bthl.healthcare.model.InsurancePlan;
import com.bthl.healthcare.model.enums.ProviderType;
import com.bthl.healthcare.model.enums.PlanTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, UUID> {

    Optional<InsurancePlan> findByPlanCode(String planCode);
    boolean existsByPlanCode(String planCode);

    List<InsurancePlan> findByProviderId(UUID providerId);
    Page<InsurancePlan> findByProviderId(UUID providerId, Pageable pageable);
    
    List<InsurancePlan> findByPlanType(ProviderType planType);
    Page<InsurancePlan> findByPlanType(ProviderType planType, Pageable pageable);
    
    List<InsurancePlan> findByTier(PlanTier tier);
    Page<InsurancePlan> findByTier(PlanTier tier, Pageable pageable);
    
    Page<InsurancePlan> findByIsActiveTrue(Pageable pageable);
    Page<InsurancePlan> findByPlanTypeAndIsActiveTrue(ProviderType planType, Pageable pageable);

    @Query("SELECT p FROM InsurancePlan p WHERE " +
           "LOWER(p.planName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.planCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.provider.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<InsurancePlan> searchPlans(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT p FROM InsurancePlan p WHERE p.deductible BETWEEN :minDeductible AND :maxDeductible")
    List<InsurancePlan> findByDeductibleRange(@Param("minDeductible") BigDecimal minDeductible,
                                             @Param("maxDeductible") BigDecimal maxDeductible);

    @Query("SELECT p FROM InsurancePlan p WHERE :coverage MEMBER OF p.geographicCoverage")
    List<InsurancePlan> findByGeographicCoverage(@Param("coverage") String coverage);

    long countByPlanType(ProviderType planType);
    long countByTier(PlanTier tier);
    long countByIsActiveTrue();
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/CompanyHealthcarePortfolioRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Company Healthcare Portfolio repository interface for portfolio data operations
 */

package com.bthl.healthcare.repository;

import com.bthl.healthcare.model.CompanyHealthcarePortfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyHealthcarePortfolioRepository extends JpaRepository<CompanyHealthcarePortfolio, UUID> {

    List<CompanyHealthcarePortfolio> findByCompanyId(UUID companyId);
    Page<CompanyHealthcarePortfolio> findByCompanyId(UUID companyId, Pageable pageable);
    
    List<CompanyHealthcarePortfolio> findByBrokerId(UUID brokerId);
    Page<CompanyHealthcarePortfolio> findByBrokerId(UUID brokerId, Pageable pageable);
    
    List<CompanyHealthcarePortfolio> findByPolicyYear(Integer policyYear);
    Page<CompanyHealthcarePortfolio> findByStatus(String status, Pageable pageable);

    @Query("SELECT p FROM CompanyHealthcarePortfolio p WHERE p.renewalDate BETWEEN :startDate AND :endDate")
    List<CompanyHealthcarePortfolio> findByRenewalDateBetween(@Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM CompanyHealthcarePortfolio p WHERE " +
           "LOWER(p.portfolioName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.company.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<CompanyHealthcarePortfolio> searchPortfolios(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.totalPremium), 0) FROM CompanyHealthcarePortfolio p WHERE p.status = 'ACTIVE'")
    java.math.BigDecimal getTotalActivePremium();

    long countByStatus(String status);
    long countByPolicyYear(Integer policyYear);
}
