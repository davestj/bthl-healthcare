/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/InsurancePlanRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-17
 * Purpose: Insurance Plan repository interface for healthcare plan data operations
 * Description: I designed this repository to manage the complex data operations surrounding
 *              healthcare insurance plans. This includes plan categorization, cost analysis,
 *              coverage details, geographic availability, and plan comparison functionality
 *              that forms the core of our healthcare management platform.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of InsurancePlanRepository interface
 * 2025-07-17: Separated into individual file following Java organizational conventions
 * 
 * Git Commit: git commit -m "feat: add InsurancePlanRepository for comprehensive plan data management"
 * 
 * Next Dev Feature: Add plan comparison algorithms and cost-benefit analysis methods
 * TODO: Implement plan recommendation engine based on company demographics and needs
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

/**
 * I created this InsurancePlanRepository interface to handle all database operations
 * related to insurance plan management. Healthcare plans are the central product
 * offerings in our platform, and this repository supports sophisticated plan
 * discovery, comparison, and analysis capabilities that help companies make
 * informed decisions about their employee healthcare benefits.
 */
@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, UUID> {

    /**
     * I find an insurance plan by its unique plan code.
     * Plan codes are standardized identifiers used throughout the
     * insurance industry for consistent plan identification and enrollment.
     * 
     * @param planCode the plan code to search for
     * @return Optional containing the plan if found
     */
    Optional<InsurancePlan> findByPlanCode(String planCode);

    /**
     * I check if a plan code already exists to prevent duplicates.
     * Plan code uniqueness is essential for maintaining data integrity
     * and preventing confusion in plan identification and enrollment.
     * 
     * @param planCode the plan code to check
     * @return true if a plan with this code exists
     */
    boolean existsByPlanCode(String planCode);

    /**
     * I find all plans offered by a specific insurance provider.
     * Provider-based plan retrieval supports provider-specific browsing
     * and helps companies explore options from preferred carriers.
     * 
     * @param providerId the provider ID to search for
     * @return List of plans from the specified provider
     */
    List<InsurancePlan> findByProviderId(UUID providerId);

    /**
     * I find plans by provider with pagination support.
     * This method supports administrative interfaces that need to display
     * large numbers of plans organized by their insurance provider.
     * 
     * @param providerId the provider ID to search for
     * @param pageable pagination parameters
     * @return Page of plans from the specified provider
     */
    Page<InsurancePlan> findByProviderId(UUID providerId, Pageable pageable);
    
    /**
     * I find all plans of a specific type for categorized browsing.
     * Plan types include health insurance, dental, vision, life insurance,
     * and disability insurance, allowing for specialized plan discovery.
     * 
     * @param planType the type of plan to find
     * @return List of plans of the specified type
     */
    List<InsurancePlan> findByPlanType(ProviderType planType);

    /**
     * I find plans by type with pagination support.
     * Type-based plan browsing with pagination supports user interfaces
     * that organize plans into logical categories for easier comparison.
     * 
     * @param planType the type of plan to find
     * @param pageable pagination parameters
     * @return Page of plans of the specified type
     */
    Page<InsurancePlan> findByPlanType(ProviderType planType, Pageable pageable);
    
    /**
     * I find all plans of a specific tier for cost-based filtering.
     * Plan tiers (Bronze, Silver, Gold, Platinum, Catastrophic) represent
     * different levels of coverage and cost-sharing arrangements.
     * 
     * @param tier the plan tier to find
     * @return List of plans in the specified tier
     */
    List<InsurancePlan> findByTier(PlanTier tier);

    /**
     * I find plans by tier with pagination support.
     * Tier-based plan browsing helps companies find plans that match
     * their desired balance of premium costs and coverage levels.
     * 
     * @param tier the plan tier to find
     * @param pageable pagination parameters
     * @return Page of plans in the specified tier
     */
    Page<InsurancePlan> findByTier(PlanTier tier, Pageable pageable);
    
    /**
     * I find all active plans for operational use.
     * Active status filtering ensures that only currently available
     * plans are presented to companies for enrollment consideration.
     * 
     * @param pageable pagination parameters
     * @return Page of active insurance plans
     */
    Page<InsurancePlan> findByIsActiveTrue(Pageable pageable);

    /**
     * I find active plans of a specific type for focused browsing.
     * This combination of type and active status filtering provides
     * the most relevant plan options for companies to evaluate.
     * 
     * @param planType the type of plan to find
     * @param pageable pagination parameters
     * @return Page of active plans of the specified type
     */
    Page<InsurancePlan> findByPlanTypeAndIsActiveTrue(ProviderType planType, Pageable pageable);

    /**
     * I search plans by name, code, or provider name.
     * This comprehensive search capability supports plan discovery
     * across multiple identifying characteristics and relationships.
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination parameters
     * @return Page of plans matching the search criteria
     */
    @Query("SELECT p FROM InsurancePlan p WHERE " +
           "LOWER(p.planName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.planCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.provider.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<InsurancePlan> searchPlans(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * I find plans within a specific deductible range for cost filtering.
     * Deductible ranges are crucial for companies that have specific
     * budget constraints or employee cost-sharing preferences.
     * 
     * @param minDeductible minimum deductible amount
     * @param maxDeductible maximum deductible amount
     * @return List of plans within the deductible range
     */
    @Query("SELECT p FROM InsurancePlan p WHERE p.deductible BETWEEN :minDeductible AND :maxDeductible")
    List<InsurancePlan> findByDeductibleRange(@Param("minDeductible") BigDecimal minDeductible,
                                             @Param("maxDeductible") BigDecimal maxDeductible);

    /**
     * I find plans that provide coverage in a specific geographic area.
     * Geographic coverage filtering ensures that companies only see plans
     * that are available to their employee locations and business operations.
     * 
     * @param coverage the geographic area to search for
     * @return List of plans covering the specified area
     */
    @Query("SELECT p FROM InsurancePlan p WHERE :coverage MEMBER OF p.geographicCoverage")
    List<InsurancePlan> findByGeographicCoverage(@Param("coverage") String coverage);

    /**
     * I count plans by type for statistical reporting.
     * Type-based plan counts provide valuable business intelligence
     * about the diversity and composition of available plan options.
     * 
     * @param planType the plan type to count
     * @return number of plans of the specified type
     */
    long countByPlanType(ProviderType planType);

    /**
     * I count plans by tier for market analysis.
     * Tier-based plan counts help understand the distribution of
     * coverage levels and pricing options in the market.
     * 
     * @param tier the plan tier to count
     * @return number of plans in the specified tier
     */
    long countByTier(PlanTier tier);

    /**
     * I count active plans for operational metrics.
     * Active plan counts are essential for monitoring market
     * availability and business health indicators.
     * 
     * @return number of active plans
     */
    long countByIsActiveTrue();
}
