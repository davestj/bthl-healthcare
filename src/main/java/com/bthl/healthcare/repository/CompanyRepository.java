/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/CompanyRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-17
 * Purpose: Company repository interface for company management data operations
 * Description: I designed this repository to handle all database operations related to company
 *              management in the healthcare platform, including company search, filtering,
 *              and statistical operations for portfolio management.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of CompanyRepository interface
 * 2025-07-17: Separated into individual file following Java organizational conventions
 * 
 * Git Commit: git commit -m "feat: add CompanyRepository interface for company data management"
 * 
 * Next Dev Feature: Add company analytics and portfolio value calculations
 * TODO: Implement company merge and acquisition data tracking capabilities
 */

package com.bthl.healthcare.repository;

import com.bthl.healthcare.model.Company;
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
 * I created this CompanyRepository interface to provide comprehensive data access
 * for company management operations. This interface supports the healthcare
 * platform's need to manage client companies and their healthcare portfolios.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    /**
     * I find a company by its unique name for business operations.
     * Company names must be unique in the system to prevent confusion
     * and ensure accurate portfolio management.
     * 
     * @param name the company name to search for
     * @return Optional containing the company if found
     */
    Optional<Company> findByName(String name);

    /**
     * I find a company by its tax identification number.
     * Tax ID is a unique identifier used for legal and financial operations,
     * essential for compliance and reporting requirements.
     * 
     * @param taxId the tax identification number to search for
     * @return Optional containing the company if found
     */
    Optional<Company> findByTaxId(String taxId);

    /**
     * I check if a company name already exists to prevent duplicates.
     * This validation ensures unique company naming across the platform.
     * 
     * @param name the company name to check
     * @return true if a company with this name exists
     */
    boolean existsByName(String name);

    /**
     * I check if a tax ID already exists to prevent duplicates.
     * This validation ensures tax ID uniqueness for compliance purposes.
     * 
     * @param taxId the tax ID to check
     * @return true if a company with this tax ID exists
     */
    boolean existsByTaxId(String taxId);

    /**
     * I find companies by their status with pagination support.
     * Status filtering is essential for managing active, inactive,
     * and suspended company accounts.
     * 
     * @param status the company status to filter by
     * @param pageable pagination parameters
     * @return Page of companies with the specified status
     */
    Page<Company> findByStatus(String status, Pageable pageable);

    /**
     * I find companies by industry classification.
     * Industry-based filtering helps with market analysis and
     * industry-specific healthcare plan recommendations.
     * 
     * @param industry the industry to filter by
     * @param pageable pagination parameters
     * @return Page of companies in the specified industry
     */
    Page<Company> findByIndustry(String industry, Pageable pageable);

    /**
     * I find companies within a specific employee count range.
     * Employee count is crucial for determining appropriate healthcare
     * plans and pricing structures for group coverage.
     * 
     * @param minEmployees minimum employee count
     * @param maxEmployees maximum employee count
     * @param pageable pagination parameters
     * @return Page of companies within the employee count range
     */
    @Query("SELECT c FROM Company c WHERE c.employeeCount BETWEEN :minEmployees AND :maxEmployees")
    Page<Company> findByEmployeeCountRange(@Param("minEmployees") Integer minEmployees, 
                                          @Param("maxEmployees") Integer maxEmployees, 
                                          Pageable pageable);

    /**
     * I search companies by name, legal name, or tax ID.
     * This comprehensive search method supports administrative interfaces
     * and client lookup functionality across multiple identifying fields.
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination parameters
     * @return Page of companies matching the search criteria
     */
    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.legalName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "c.taxId LIKE CONCAT('%', :searchTerm, '%')")
    Page<Company> searchCompanies(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * I find the top 10 companies by employee count for reporting.
     * This method supports dashboard analytics and market analysis
     * by identifying the largest client companies.
     * 
     * @return List of top 10 companies by employee count
     */
    List<Company> findTop10ByOrderByEmployeeCountDesc();
    
    /**
     * I count companies by status for statistical reporting.
     * Status-based counting provides valuable insights for business
     * analytics and operational reporting.
     * 
     * @param status the status to count
     * @return number of companies with the specified status
     */
    @Query("SELECT COUNT(c) FROM Company c WHERE c.status = :status")
    long countByStatus(@Param("status") String status);
}
