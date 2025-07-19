/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/InsuranceProviderRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-17
 * Purpose: Insurance Provider repository interface for provider data operations
 * Description: I designed this repository to manage insurance provider data including provider
 *              search, classification by type, service area management, and provider analytics.
 *              This interface is essential for managing the network of insurance companies
 *              that offer healthcare plans through our platform.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of InsuranceProviderRepository interface
 * 2025-07-17: Separated into individual file following Java organizational conventions
 * 
 * Git Commit: git commit -m "feat: add InsuranceProviderRepository for provider data management"
 * 
 * Next Dev Feature: Add provider rating and performance analytics methods
 * TODO: Implement provider network adequacy and coverage gap analysis
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

/**
 * I created this InsuranceProviderRepository interface to handle all database operations
 * related to insurance provider management. This repository supports the healthcare
 * platform's need to maintain relationships with various insurance companies and
 * track their capabilities, service areas, and performance metrics.
 */
@Repository
public interface InsuranceProviderRepository extends JpaRepository<InsuranceProvider, UUID> {

    /**
     * I find an insurance provider by its unique provider code.
     * Provider codes are standardized identifiers used throughout the
     * insurance industry for consistent provider identification.
     * 
     * @param providerCode the provider code to search for
     * @return Optional containing the provider if found
     */
    Optional<InsuranceProvider> findByProviderCode(String providerCode);

    /**
     * I check if a provider code already exists to prevent duplicates.
     * Provider code uniqueness is essential for maintaining data integrity
     * and preventing confusion in provider identification.
     * 
     * @param providerCode the provider code to check
     * @return true if a provider with this code exists
     */
    boolean existsByProviderCode(String providerCode);

    /**
     * I check if a provider name already exists to prevent duplicates.
     * Provider name uniqueness helps maintain clear identification
     * and prevents administrative confusion.
     * 
     * @param name the provider name to check
     * @return true if a provider with this name exists
     */
    boolean existsByName(String name);

    /**
     * I find all providers of a specific type for categorization.
     * Provider types include health insurance, dental, vision, life insurance,
     * and disability insurance, allowing for specialized management.
     * 
     * @param providerType the type of provider to find
     * @return List of providers of the specified type
     */
    List<InsuranceProvider> findByProviderType(ProviderType providerType);

    /**
     * I find providers by type with pagination support.
     * This method supports administrative interfaces that need to display
     * large numbers of providers organized by their service type.
     * 
     * @param providerType the type of provider to find
     * @param pageable pagination parameters
     * @return Page of providers of the specified type
     */
    Page<InsuranceProvider> findByProviderType(ProviderType providerType, Pageable pageable);

    /**
     * I find all active providers for operational use.
     * Active status filtering ensures that only currently available
     * providers are presented to users and included in plan offerings.
     * 
     * @param pageable pagination parameters
     * @return Page of active insurance providers
     */
    Page<InsuranceProvider> findByIsActiveTrue(Pageable pageable);

    /**
     * I search providers by name, legal name, or provider code.
     * This comprehensive search capability supports administrative
     * functions and provider lookup across multiple identifying fields.
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination parameters
     * @return Page of providers matching the search criteria
     */
    @Query("SELECT p FROM InsuranceProvider p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.legalName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.providerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<InsuranceProvider> searchProviders(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * I find providers that serve a specific geographic area.
     * Service area filtering is crucial for matching companies with
     * providers that can serve their employee locations.
     * 
     * @param serviceArea the geographic area to search for
     * @return List of providers serving the specified area
     */
    @Query("SELECT p FROM InsuranceProvider p WHERE :serviceArea MEMBER OF p.serviceAreas")
    List<InsuranceProvider> findByServiceArea(@Param("serviceArea") String serviceArea);

    /**
     * I find the top 10 providers by network size for analysis.
     * Network size is a key indicator of provider capability and
     * member access to healthcare services.
     * 
     * @return List of top 10 providers by network size
     */
    List<InsuranceProvider> findTop10ByOrderByNetworkSizeDesc();
    
    /**
     * I count providers by type for statistical reporting.
     * Type-based counting provides insights into the diversity
     * and composition of the provider network.
     * 
     * @param providerType the provider type to count
     * @return number of providers of the specified type
     */
    long countByProviderType(ProviderType providerType);

    /**
     * I count active providers for operational metrics.
     * Active provider counts are essential for monitoring
     * network adequacy and business health.
     * 
     * @return number of active providers
     */
    long countByIsActiveTrue();
}
