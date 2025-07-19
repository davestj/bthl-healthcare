/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/InsuranceBrokerRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-17
 * Purpose: Insurance Broker repository interface for broker data operations
 * Description: I designed this repository to manage licensed insurance brokers who serve as
 *              intermediaries between companies and insurance providers. This interface handles
 *              broker licensing, territory management, specialization tracking, and performance
 *              monitoring essential for maintaining a qualified broker network.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of InsuranceBrokerRepository interface
 * 2025-07-17: Separated into individual file following Java organizational conventions
 * 
 * Git Commit: git commit -m "feat: add InsuranceBrokerRepository for broker management operations"
 * 
 * Next Dev Feature: Add broker performance metrics and commission tracking methods
 * TODO: Implement broker certification renewal alerts and compliance monitoring
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

/**
 * I created this InsuranceBrokerRepository interface to handle all database operations
 * related to insurance broker management. Licensed brokers are crucial intermediaries
 * in the healthcare insurance ecosystem, and this repository supports tracking their
 * credentials, territories, specializations, and regulatory compliance.
 */
@Repository
public interface InsuranceBrokerRepository extends JpaRepository<InsuranceBroker, UUID> {

    /**
     * I find a broker by their associated user ID for profile management.
     * This relationship links broker-specific data with the general user
     * authentication and profile information in the system.
     * 
     * @param userId the user ID to search for
     * @return Optional containing the broker if found
     */
    Optional<InsuranceBroker> findByUserId(UUID userId);

    /**
     * I find a broker by their license number for regulatory verification.
     * License numbers are unique identifiers issued by state insurance
     * departments and are essential for compliance verification.
     * 
     * @param licenseNumber the license number to search for
     * @return Optional containing the broker if found
     */
    Optional<InsuranceBroker> findByLicenseNumber(String licenseNumber);

    /**
     * I check if a license number already exists to prevent duplicates.
     * License number uniqueness is critical for regulatory compliance
     * and preventing fraudulent broker registrations.
     * 
     * @param licenseNumber the license number to check
     * @return true if a broker with this license number exists
     */
    boolean existsByLicenseNumber(String licenseNumber);

    /**
     * I find all brokers licensed in a specific state.
     * State-based filtering is essential for regulatory compliance
     * since insurance licenses are issued by individual states.
     * 
     * @param licenseState the state where the license was issued
     * @return List of brokers licensed in the specified state
     */
    List<InsuranceBroker> findByLicenseState(String licenseState);

    /**
     * I find all active brokers with pagination support.
     * Active status filtering ensures that only currently available
     * and compliant brokers are presented for business operations.
     * 
     * @param pageable pagination parameters
     * @return Page of active insurance brokers
     */
    Page<InsuranceBroker> findByIsActiveTrue(Pageable pageable);

    /**
     * I find brokers whose licenses expire within a date range.
     * License expiration monitoring is crucial for maintaining
     * compliance and ensuring continuous broker eligibility.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return List of brokers with licenses expiring in the specified period
     */
    @Query("SELECT b FROM InsuranceBroker b WHERE b.licenseExpires BETWEEN :startDate AND :endDate")
    List<InsuranceBroker> findByLicenseExpiringBetween(@Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate);

    /**
     * I find brokers who serve a specific territory.
     * Territory-based searches help match brokers with companies
     * in their designated service areas for more effective support.
     * 
     * @param territory the territory to search for
     * @return List of brokers serving the specified territory
     */
    @Query("SELECT b FROM InsuranceBroker b WHERE :territory MEMBER OF b.territories")
    List<InsuranceBroker> findByTerritory(@Param("territory") String territory);

    /**
     * I find brokers with a specific specialization.
     * Specialization-based searches allow companies to find brokers
     * with expertise in their industry or specific insurance needs.
     * 
     * @param specialization the specialization to search for
     * @return List of brokers with the specified specialization
     */
    @Query("SELECT b FROM InsuranceBroker b WHERE :specialization MEMBER OF b.specializations")
    List<InsuranceBroker> findBySpecialization(@Param("specialization") String specialization);

    /**
     * I search brokers by name, agency name, or license number.
     * This comprehensive search supports administrative functions
     * and broker lookup across multiple identifying characteristics.
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination parameters
     * @return Page of brokers matching the search criteria
     */
    @Query("SELECT b FROM InsuranceBroker b WHERE " +
           "LOWER(b.user.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.user.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.agencyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "b.licenseNumber LIKE CONCAT('%', :searchTerm, '%')")
    Page<InsuranceBroker> searchBrokers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * I count active brokers for operational metrics.
     * Active broker counts provide important business intelligence
     * about network capacity and service availability.
     * 
     * @return number of active brokers
     */
    long countByIsActiveTrue();

    /**
     * I count brokers by state for regulatory reporting.
     * State-based broker counts support compliance reporting
     * and network adequacy analysis by geographic region.
     * 
     * @param licenseState the state to count brokers for
     * @return number of brokers licensed in the specified state
     */
    long countByLicenseState(String licenseState);
}
