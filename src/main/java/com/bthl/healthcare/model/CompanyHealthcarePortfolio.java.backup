/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/CompanyHealthcarePortfolio.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Company Healthcare Portfolio entity for managing company insurance portfolios
 * Description: I designed this entity to represent a company's complete healthcare coverage portfolio
 *              for a specific policy year, including all selected plans, broker relationships,
 *              and comprehensive portfolio analytics.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of CompanyHealthcarePortfolio entity with comprehensive portfolio management
 * 
 * Git Commit: git commit -m "feat: add CompanyHealthcarePortfolio entity for complete portfolio management"
 * 
 * Next Dev Feature: Add portfolio optimization algorithms and renewal prediction analytics
 * TODO: Implement automated renewal workflows and cost trend analysis
 */

package com.bthl.healthcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * I created this CompanyHealthcarePortfolio entity to represent a company's
 * complete healthcare coverage for a specific policy year. I've designed it
 * to track all aspects of the company's insurance program and employee coverage.
 */
@Entity
@Table(name = "company_healthcare_portfolios", indexes = {
    @Index(name = "idx_portfolios_company", columnList = "company_id"),
    @Index(name = "idx_portfolios_broker", columnList = "broker_id"),
    @Index(name = "idx_portfolios_year", columnList = "policy_year"),
    @Index(name = "idx_portfolios_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class CompanyHealthcarePortfolio {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    public Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "broker_id")
    public InsuranceBroker broker;

    @NotBlank(message = "Portfolio name is required")
    @Size(max = 255, message = "Portfolio name must not exceed 255 characters")
    @Column(name = "portfolio_name", nullable = false, length = 255)
    public String portfolioName;

    @NotNull(message = "Policy year is required")
    @Min(value = 2020, message = "Policy year must be 2020 or later")
    @Max(value = 2030, message = "Policy year must not exceed 2030")
    @Column(name = "policy_year", nullable = false)
    public Integer policyYear;

    @NotNull(message = "Effective date is required")
    @Column(name = "effective_date", nullable = false)
    public LocalDate effectiveDate;

    @NotNull(message = "Renewal date is required")
    @Column(name = "renewal_date", nullable = false)
    public LocalDate renewalDate;

    @DecimalMin(value = "0.00", message = "Total premium must be non-negative")
    @DecimalMax(value = "99999999999.99", message = "Total premium must not exceed $99,999,999,999.99")
    @Column(name = "total_premium", precision = 15, scale = 2)
    public BigDecimal totalPremium;

    @DecimalMin(value = "0.00", message = "Employee contribution percentage must be non-negative")
    @DecimalMax(value = "100.00", message = "Employee contribution percentage must not exceed 100%")
    @Column(name = "employee_contribution_percentage", precision = 5, scale = 2)
    public BigDecimal employeeContributionPercentage;

    @Min(value = 0, message = "Total employees covered must be non-negative")
    @Column(name = "total_employees_covered")
    public Integer totalEmployeesCovered;

    @Min(value = 0, message = "Total dependents covered must be non-negative")
    @Column(name = "total_dependents_covered")
    public Integer totalDependentsCovered;

    @Column(name = "wellness_program", nullable = false)
    public Boolean wellnessProgram = false;

    @DecimalMin(value = "0.0000", message = "Wellness discount must be non-negative")
    @DecimalMax(value = "1.0000", message = "Wellness discount must not exceed 100%")
    @Column(name = "wellness_discount", precision = 5, scale = 4)
    public BigDecimal wellnessDiscount;

    @Column(name = "claims_history", columnDefinition = "jsonb")
    public Map<String, Object> claimsHistory = new HashMap<>();

    @Column(name = "risk_assessment", columnDefinition = "jsonb")
    public Map<String, Object> riskAssessment = new HashMap<>();

    @Pattern(regexp = "^(ACTIVE|INACTIVE|RENEWAL|TERMINATED|PENDING)$", message = "Status must be ACTIVE, INACTIVE, RENEWAL, TERMINATED, or PENDING")
    @Column(name = "status", length = 20)
    public String status = "PENDING";

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    public String notes;

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

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<PortfolioPlan> portfolioPlans = new HashSet<>();

    // I implement default constructor for JPA
    public CompanyHealthcarePortfolio() {}

    // I create a comprehensive constructor for portfolio creation
    public CompanyHealthcarePortfolio(Company company, String portfolioName, Integer policyYear,
                                    LocalDate effectiveDate, LocalDate renewalDate, InsuranceBroker broker) {
        this.company = company;
        this.portfolioName = portfolioName;
        this.policyYear = policyYear;
        this.effectiveDate = effectiveDate;
        this.renewalDate = renewalDate;
        this.broker = broker;
        this.status = "PENDING";
        this.wellnessProgram = false;
    }

    // I implement getter and setter methods following my naming conventions

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public InsuranceBroker getBroker() {
        return broker;
    }

    public void setBroker(InsuranceBroker broker) {
        this.broker = broker;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public Integer getPolicyYear() {
        return policyYear;
    }

    public void setPolicyYear(Integer policyYear) {
        this.policyYear = policyYear;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(LocalDate renewalDate) {
        this.renewalDate = renewalDate;
    }

    public BigDecimal getTotalPremium() {
        return totalPremium;
    }

    public void setTotalPremium(BigDecimal totalPremium) {
        this.totalPremium = totalPremium;
    }

    public BigDecimal getEmployeeContributionPercentage() {
        return employeeContributionPercentage;
    }

    public void setEmployeeContributionPercentage(BigDecimal employeeContributionPercentage) {
        this.employeeContributionPercentage = employeeContributionPercentage;
    }

    public Integer getTotalEmployeesCovered() {
        return totalEmployeesCovered;
    }

    public void setTotalEmployeesCovered(Integer totalEmployeesCovered) {
        this.totalEmployeesCovered = totalEmployeesCovered;
    }

    public Integer getTotalDependentsCovered() {
        return totalDependentsCovered;
    }

    public void setTotalDependentsCovered(Integer totalDependentsCovered) {
        this.totalDependentsCovered = totalDependentsCovered;
    }

    public Boolean getWellnessProgram() {
        return wellnessProgram;
    }

    public void setWellnessProgram(Boolean wellnessProgram) {
        this.wellnessProgram = wellnessProgram;
    }

    public BigDecimal getWellnessDiscount() {
        return wellnessDiscount;
    }

    public void setWellnessDiscount(BigDecimal wellnessDiscount) {
        this.wellnessDiscount = wellnessDiscount;
    }

    public Map<String, Object> getClaimsHistory() {
        return new HashMap<>(claimsHistory);
    }

    public void setClaimsHistory(Map<String, Object> claimsHistory) {
        this.claimsHistory = claimsHistory != null ? new HashMap<>(claimsHistory) : new HashMap<>();
    }

    public Map<String, Object> getRiskAssessment() {
        return new HashMap<>(riskAssessment);
    }

    public void setRiskAssessment(Map<String, Object> riskAssessment) {
        this.riskAssessment = riskAssessment != null ? new HashMap<>(riskAssessment) : new HashMap<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Set<PortfolioPlan> getPortfolioPlans() {
        return new HashSet<>(portfolioPlans);
    }

    public void setPortfolioPlans(Set<PortfolioPlan> portfolioPlans) {
        this.portfolioPlans = portfolioPlans != null ? new HashSet<>(portfolioPlans) : new HashSet<>();
    }

    // I create convenient computed properties for frontend use

    @JsonProperty("displayName")
    public String getDisplayName() {
        String companyName = company != null ? company.getName() : "Unknown Company";
        return companyName + " - " + portfolioName + " (" + policyYear + ")";
    }

    @JsonProperty("totalCovered")
    public Integer getTotalCovered() {
        int employees = totalEmployeesCovered != null ? totalEmployeesCovered : 0;
        int dependents = totalDependentsCovered != null ? totalDependentsCovered : 0;
        return employees + dependents;
    }

    @JsonProperty("averagePremiumPerPerson")
    public BigDecimal getAveragePremiumPerPerson() {
        if (totalPremium == null || getTotalCovered() == 0) return BigDecimal.ZERO;
        return totalPremium.divide(new BigDecimal(getTotalCovered()), 2, java.math.RoundingMode.HALF_UP);
    }

    @JsonProperty("employerContribution")
    public BigDecimal getEmployerContribution() {
        if (totalPremium == null || employeeContributionPercentage == null) return BigDecimal.ZERO;
        BigDecimal employeeContribution = totalPremium.multiply(employeeContributionPercentage.divide(new BigDecimal("100")));
        return totalPremium.subtract(employeeContribution);
    }

    @JsonProperty("employeeContribution")
    public BigDecimal getEmployeeContribution() {
        if (totalPremium == null || employeeContributionPercentage == null) return BigDecimal.ZERO;
        return totalPremium.multiply(employeeContributionPercentage.divide(new BigDecimal("100")));
    }

    @JsonProperty("daysUntilRenewal")
    public Long getDaysUntilRenewal() {
        return renewalDate != null ? java.time.ChronoUnit.DAYS.between(LocalDate.now(), renewalDate) : null;
    }

    @JsonProperty("isActive")
    public boolean getIsActive() {
        return "ACTIVE".equals(status);
    }

    @JsonProperty("isRenewalDue")
    public boolean getIsRenewalDue() {
        return renewalDate != null && renewalDate.isBefore(LocalDate.now().plusDays(90));
    }

    @JsonProperty("planCount")
    public int getPlanCount() {
        return portfolioPlans != null ? portfolioPlans.size() : 0;
    }

    @JsonProperty("dependencyRatio")
    public BigDecimal getDependencyRatio() {
        if (totalEmployeesCovered == null || totalEmployeesCovered == 0) return BigDecimal.ZERO;
        int dependents = totalDependentsCovered != null ? totalDependentsCovered : 0;
        return new BigDecimal(dependents).divide(new BigDecimal(totalEmployeesCovered), 2, java.math.RoundingMode.HALF_UP);
    }

    @JsonProperty("wellnessDiscountAmount")
    public BigDecimal getWellnessDiscountAmount() {
        if (totalPremium == null || wellnessDiscount == null) return BigDecimal.ZERO;
        return totalPremium.multiply(wellnessDiscount);
    }

    @JsonProperty("statusClass")
    public String getStatusClass() {
        return switch (status != null ? status : "PENDING") {
            case "ACTIVE" -> "status-active";
            case "INACTIVE" -> "status-inactive";
            case "RENEWAL" -> "status-renewal";
            case "TERMINATED" -> "status-terminated";
            case "PENDING" -> "status-pending";
            default -> "status-unknown";
        };
    }

    // I implement business logic methods for portfolio management

    public void activate() {
        this.status = "ACTIVE";
    }

    public void deactivate() {
        this.status = "INACTIVE";
    }

    public void markForRenewal() {
        this.status = "RENEWAL";
    }

    public void terminate() {
        this.status = "TERMINATED";
    }

    public void addPlan(PortfolioPlan portfolioPlan) {
        if (portfolioPlan != null) {
            if (portfolioPlans == null) {
                portfolioPlans = new HashSet<>();
            }
            portfolioPlans.add(portfolioPlan);
            portfolioPlan.portfolio = this;
            recalculateTotals();
        }
    }

    public void removePlan(PortfolioPlan portfolioPlan) {
        if (portfolioPlans != null && portfolioPlan != null) {
            portfolioPlans.remove(portfolioPlan);
            portfolioPlan.portfolio = null;
            recalculateTotals();
        }
    }

    public void recalculateTotals() {
        if (portfolioPlans == null || portfolioPlans.isEmpty()) {
            totalEmployeesCovered = 0;
            totalDependentsCovered = 0;
            return;
        }

        int employees = portfolioPlans.stream()
            .mapToInt(pp -> pp.employeesEnrolled != null ? pp.employeesEnrolled : 0)
            .sum();
        
        int dependents = portfolioPlans.stream()
            .mapToInt(pp -> pp.dependentsEnrolled != null ? pp.dependentsEnrolled : 0)
            .sum();

        this.totalEmployeesCovered = employees;
        this.totalDependentsCovered = dependents;
    }

    public void updateClaimsHistory(String period, Object claims) {
        if (period != null && !period.trim().isEmpty()) {
            if (claimsHistory == null) {
                claimsHistory = new HashMap<>();
            }
            claimsHistory.put(period, claims);
        }
    }

    public void updateRiskAssessment(String factor, Object assessment) {
        if (factor != null && !factor.trim().isEmpty()) {
            if (riskAssessment == null) {
                riskAssessment = new HashMap<>();
            }
            riskAssessment.put(factor, assessment);
        }
    }

    public boolean hasCompleteInformation() {
        return company != null &&
               portfolioName != null && !portfolioName.trim().isEmpty() &&
               policyYear != null &&
               effectiveDate != null &&
               renewalDate != null &&
               totalPremium != null &&
               portfolioPlans != null && !portfolioPlans.isEmpty();
    }

    // I implement equals and hashCode based on UUID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CompanyHealthcarePortfolio portfolio = (CompanyHealthcarePortfolio) obj;
        return Objects.equals(id, portfolio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CompanyHealthcarePortfolio{" +
               "id=" + id +
               ", company=" + (company != null ? company.getName() : "null") +
               ", portfolioName='" + portfolioName + '\'' +
               ", policyYear=" + policyYear +
               ", status='" + status + '\'' +
               ", totalCovered=" + getTotalCovered() +
               ", planCount=" + getPlanCount() +
               ", createdAt=" + createdAt +
               '}';
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/PortfolioPlan.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Portfolio Plan junction entity for many-to-many relationship between portfolios and plans
 * Description: I designed this entity to represent the relationship between a company's healthcare
 *              portfolio and specific insurance plans, including enrollment numbers and pricing.
 */

@Entity
@Table(name = "portfolio_plans", indexes = {
    @Index(name = "idx_portfolio_plans_portfolio", columnList = "portfolio_id"),
    @Index(name = "idx_portfolio_plans_plan", columnList = "plan_id")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"portfolio_id", "plan_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class PortfolioPlan {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolio_id", nullable = false)
    public CompanyHealthcarePortfolio portfolio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    public InsurancePlan plan;

    @DecimalMin(value = "0.00", message = "Employee premium must be non-negative")
    @DecimalMax(value = "99999.99", message = "Employee premium must not exceed $99,999.99")
    @Column(name = "employee_premium", precision = 10, scale = 2)
    public BigDecimal employeePremium;

    @DecimalMin(value = "0.00", message = "Family premium must be non-negative")
    @DecimalMax(value = "99999.99", message = "Family premium must not exceed $99,999.99")
    @Column(name = "family_premium", precision = 10, scale = 2)
    public BigDecimal familyPremium;

    @Min(value = 0, message = "Employees enrolled must be non-negative")
    @Column(name = "employees_enrolled")
    public Integer employeesEnrolled = 0;

    @Min(value = 0, message = "Dependents enrolled must be non-negative")
    @Column(name = "dependents_enrolled")
    public Integer dependentsEnrolled = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    // I implement default constructor for JPA
    public PortfolioPlan() {}

    // I create constructor for portfolio plan creation
    public PortfolioPlan(CompanyHealthcarePortfolio portfolio, InsurancePlan plan,
                        BigDecimal employeePremium, BigDecimal familyPremium) {
        this.portfolio = portfolio;
        this.plan = plan;
        this.employeePremium = employeePremium;
        this.familyPremium = familyPremium;
        this.employeesEnrolled = 0;
        this.dependentsEnrolled = 0;
    }

    // I implement getter and setter methods
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public CompanyHealthcarePortfolio getPortfolio() { return portfolio; }
    public void setPortfolio(CompanyHealthcarePortfolio portfolio) { this.portfolio = portfolio; }
    
    public InsurancePlan getPlan() { return plan; }
    public void setPlan(InsurancePlan plan) { this.plan = plan; }
    
    public BigDecimal getEmployeePremium() { return employeePremium; }
    public void setEmployeePremium(BigDecimal employeePremium) { this.employeePremium = employeePremium; }
    
    public BigDecimal getFamilyPremium() { return familyPremium; }
    public void setFamilyPremium(BigDecimal familyPremium) { this.familyPremium = familyPremium; }
    
    public Integer getEmployeesEnrolled() { return employeesEnrolled; }
    public void setEmployeesEnrolled(Integer employeesEnrolled) { this.employeesEnrolled = employeesEnrolled; }
    
    public Integer getDependentsEnrolled() { return dependentsEnrolled; }
    public void setDependentsEnrolled(Integer dependentsEnrolled) { this.dependentsEnrolled = dependentsEnrolled; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }

    @JsonProperty("totalEnrolled")
    public Integer getTotalEnrolled() {
        int employees = employeesEnrolled != null ? employeesEnrolled : 0;
        int dependents = dependentsEnrolled != null ? dependentsEnrolled : 0;
        return employees + dependents;
    }

    @JsonProperty("totalPremiumCost")
    public BigDecimal getTotalPremiumCost() {
        BigDecimal empCost = employeePremium != null && employeesEnrolled != null ? 
            employeePremium.multiply(new BigDecimal(employeesEnrolled)) : BigDecimal.ZERO;
        BigDecimal famCost = familyPremium != null && dependentsEnrolled != null ? 
            familyPremium.multiply(new BigDecimal(dependentsEnrolled)) : BigDecimal.ZERO;
        return empCost.add(famCost);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PortfolioPlan that = (PortfolioPlan) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/BrokerProviderRelationship.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Broker Provider Relationship entity for managing broker-provider partnerships
 * Description: I designed this entity to track the relationships between insurance brokers
 *              and insurance providers, including contract terms and commission structures.
 */

@Entity
@Table(name = "broker_provider_relationships", indexes = {
    @Index(name = "idx_broker_provider_broker", columnList = "broker_id"),
    @Index(name = "idx_broker_provider_provider", columnList = "provider_id"),
    @Index(name = "idx_broker_provider_active", columnList = "is_active")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"broker_id", "provider_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class BrokerProviderRelationship {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "broker_id", nullable = false)
    public InsuranceBroker broker;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", nullable = false)
    public InsuranceProvider provider;

    @Pattern(regexp = "^(CONTRACTED|APPOINTED|TERMINATED|SUSPENDED)$", message = "Relationship type must be CONTRACTED, APPOINTED, TERMINATED, or SUSPENDED")
    @Column(name = "relationship_type", length = 50)
    public String relationshipType = "CONTRACTED";

    @DecimalMin(value = "0.0000", message = "Commission rate must be non-negative")
    @DecimalMax(value = "1.0000", message = "Commission rate must not exceed 100%")
    @Column(name = "commission_rate", precision = 5, scale = 4)
    public BigDecimal commissionRate;

    @Column(name = "contract_start_date")
    public LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    public LocalDate contractEndDate;

    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    // I implement default constructor for JPA
    public BrokerProviderRelationship() {}

    // I create constructor for relationship creation
    public BrokerProviderRelationship(InsuranceBroker broker, InsuranceProvider provider,
                                    String relationshipType, BigDecimal commissionRate) {
        this.broker = broker;
        this.provider = provider;
        this.relationshipType = relationshipType;
        this.commissionRate = commissionRate;
        this.isActive = true;
    }

    // I implement getter and setter methods
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public InsuranceBroker getBroker() { return broker; }
    public void setBroker(InsuranceBroker broker) { this.broker = broker; }
    
    public InsuranceProvider getProvider() { return provider; }
    public void setProvider(InsuranceProvider provider) { this.provider = provider; }
    
    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
    
    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
    
    public LocalDate getContractStartDate() { return contractStartDate; }
    public void setContractStartDate(LocalDate contractStartDate) { this.contractStartDate = contractStartDate; }
    
    public LocalDate getContractEndDate() { return contractEndDate; }
    public void setContractEndDate(LocalDate contractEndDate) { this.contractEndDate = contractEndDate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @JsonProperty("contractStatus")
    public String getContractStatus() {
        if (contractEndDate == null) return "Open";
        LocalDate now = LocalDate.now();
        if (contractEndDate.isBefore(now)) return "Expired";
        if (contractEndDate.isBefore(now.plusDays(30))) return "Expiring Soon";
        return "Active";
    }

    public void activate() { this.isActive = true; }
    public void deactivate() { this.isActive = false; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BrokerProviderRelationship that = (BrokerProviderRelationship) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
