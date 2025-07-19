/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/repository/CompanyHealthcarePortfolioRepository.java
 * Author: davestj (David St John)
 * Date: 2025-07-17
 * Purpose: Company Healthcare Portfolio repository interface for portfolio data operations
 * Description: I designed this repository to manage the complex relationships between companies
 *              and their healthcare benefit portfolios. This interface handles portfolio
 *              tracking, renewal management, premium calculations, and performance analytics
 *              that are essential for comprehensive healthcare benefit administration.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of CompanyHealthcarePortfolioRepository interface
 * 2025-07-17: Separated into individual file following Java organizational conventions
 * 
 * Git Commit: git commit -m "feat: add CompanyHealthcarePortfolioRepository for portfolio management"
 * 
 * Next Dev Feature: Add portfolio optimization algorithms and cost-trend analysis methods
 * TODO: Implement automated renewal notifications and portfolio performance benchmarking
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

/**
 * I created this CompanyHealthcarePortfolioRepository interface to handle all database
 * operations related to company healthcare portfolio management. Healthcare portfolios
 * represent the complete collection of insurance plans and benefits that companies
 * provide to their employees, and this repository supports the complex analytics and
 * management operations needed to optimize these benefit packages.
 */
@Repository
public interface CompanyHealthcarePortfolioRepository extends JpaRepository<CompanyHealthcarePortfolio, UUID> {

    /**
     * I find all portfolios belonging to a specific company.
     * Company-based portfolio retrieval supports company-specific management
     * and allows administrators to view all healthcare benefit arrangements
     * for a particular client organization.
     * 
     * @param companyId the company ID to search for
     * @return List of portfolios belonging to the specified company
     */
    List<CompanyHealthcarePortfolio> findByCompanyId(UUID companyId);

    /**
     * I find company portfolios with pagination support.
     * This method supports administrative interfaces that need to display
     * large numbers of portfolios for companies with complex benefit structures.
     * 
     * @param companyId the company ID to search for
     * @param pageable pagination parameters
     * @return Page of portfolios belonging to the specified company
     */
    Page<CompanyHealthcarePortfolio> findByCompanyId(UUID companyId, Pageable pageable);
    
    /**
     * I find all portfolios managed by a specific broker.
     * Broker-based portfolio retrieval supports broker performance tracking
     * and allows brokers to manage all their client portfolios efficiently.
     * 
     * @param brokerId the broker ID to search for
     * @return List of portfolios managed by the specified broker
     */
    List<CompanyHealthcarePortfolio> findByBrokerId(UUID brokerId);

    /**
     * I find broker portfolios with pagination support.
     * This method enables brokers to navigate through large client portfolios
     * with efficient pagination for better user experience and system performance.
     * 
     * @param brokerId the broker ID to search for
     * @param pageable pagination parameters
     * @return Page of portfolios managed by the specified broker
     */
    Page<CompanyHealthcarePortfolio> findByBrokerId(UUID brokerId, Pageable pageable);
    
    /**
     * I find all portfolios for a specific policy year.
     * Policy year filtering is essential for annual benefit administration,
     * renewal planning, and year-over-year performance comparison analysis.
     * 
     * @param policyYear the policy year to search for
     * @return List of portfolios for the specified policy year
     */
    List<CompanyHealthcarePortfolio> findByPolicyYear(Integer policyYear);

    /**
     * I find portfolios by status with pagination support.
     * Status-based filtering helps administrators manage portfolios through
     * their various lifecycle stages including active, renewal, and terminated states.
     * 
     * @param status the portfolio status to filter by
     * @param pageable pagination parameters
     * @return Page of portfolios with the specified status
     */
    Page<CompanyHealthcarePortfolio> findByStatus(String status, Pageable pageable);

    /**
     * I find portfolios with renewal dates within a specific date range.
     * Renewal date tracking is crucial for proactive portfolio management,
     * ensuring timely renewal processing and preventing coverage gaps.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return List of portfolios with renewals in the specified period
     */
    @Query("SELECT p FROM CompanyHealthcarePortfolio p WHERE p.renewalDate BETWEEN :startDate AND :endDate")
    List<CompanyHealthcarePortfolio> findByRenewalDateBetween(@Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    /**
     * I search portfolios by name or company name.
     * This comprehensive search capability supports administrative functions
     * and portfolio lookup across multiple identifying characteristics.
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination parameters
     * @return Page of portfolios matching the search criteria
     */
    @Query("SELECT p FROM CompanyHealthcarePortfolio p WHERE " +
           "LOWER(p.portfolioName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.company.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<CompanyHealthcarePortfolio> searchPortfolios(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * I calculate the total premium value of all active portfolios.
     * This aggregation method provides critical business intelligence about
     * the total value of managed healthcare benefits across all active clients.
     * 
     * @return Total premium value of all active portfolios
     */
    @Query("SELECT COALESCE(SUM(p.totalPremium), 0) FROM CompanyHealthcarePortfolio p WHERE p.status = 'ACTIVE'")
    java.math.BigDecimal getTotalActivePremium();

    /**
     * I count portfolios by status for statistical reporting.
     * Status-based portfolio counts provide valuable business metrics
     * about portfolio distribution and operational health indicators.
     * 
     * @param status the status to count
     * @return number of portfolios with the specified status
     */
    long countByStatus(String status);

    /**
     * I count portfolios by policy year for trend analysis.
     * Year-based portfolio counts support business planning and growth
     * analysis by tracking portfolio volume across different policy periods.
     * 
     * @param policyYear the policy year to count
     * @return number of portfolios for the specified policy year
     */
    long countByPolicyYear(Integer policyYear);
}
