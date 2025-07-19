/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/PortfolioPlan.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: JPA entity representing portfolio plan relationships for BTHL-HealthCare platform
 * Description: I created this entity to manage the many-to-many relationship between company
 *              healthcare portfolios and insurance plans. I designed this to track enrollment
 *              numbers, premium costs, and plan-specific metrics for healthcare benefit management.
 * 
 * Changelog:
 * 2025-07-18: Extracted from CompanyHealthcarePortfolio.java during file separation compliance
 * 2025-07-16: Initial creation as embedded class in CompanyHealthcarePortfolio entity
 * 
 * Git Commit: git commit -m "refactor: extract PortfolioPlan entity to separate file for Java compliance"
 * 
 * Next Dev Feature: Add portfolio plan analytics and cost comparison utilities
 * TODO: Implement plan performance metrics and enrollment trend analysis
 */

package com.bthl.healthcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * I implement this PortfolioPlan entity to represent the relationship between company
 * healthcare portfolios and insurance plans with specific enrollment and pricing data.
 */
@Entity
@Table(name = "portfolio_plans", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_id", "plan_id"}))
public class PortfolioPlan {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private CompanyHealthcarePortfolio portfolio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private InsurancePlan plan;

    @DecimalMin(value = "0.00", message = "Employee premium must be non-negative")
    @DecimalMax(value = "99999.99", message = "Employee premium must not exceed $99,999.99")
    @Column(name = "employee_premium", precision = 10, scale = 2)
    private BigDecimal employeePremium;

    @DecimalMin(value = "0.00", message = "Family premium must be non-negative")
    @DecimalMax(value = "99999.99", message = "Family premium must not exceed $99,999.99")
    @Column(name = "family_premium", precision = 10, scale = 2)
    private BigDecimal familyPremium;

    @Min(value = 0, message = "Employees enrolled must be non-negative")
    @Column(name = "employees_enrolled")
    private Integer employeesEnrolled = 0;

    @Min(value = 0, message = "Dependents enrolled must be non-negative")
    @Column(name = "dependents_enrolled")
    private Integer dependentsEnrolled = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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

    // I implement getter and setter methods for all properties
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

    // I implement business logic methods for portfolio plan management
    @JsonProperty("totalEnrolled")
    public Integer getTotalEnrolled() {
        return (employeesEnrolled != null ? employeesEnrolled : 0) + 
               (dependentsEnrolled != null ? dependentsEnrolled : 0);
    }

    @JsonProperty("totalPremiumCost")
    public BigDecimal getTotalPremiumCost() {
        BigDecimal empCost = employeePremium != null ? 
            employeePremium.multiply(BigDecimal.valueOf(employeesEnrolled != null ? employeesEnrolled : 0)) : 
            BigDecimal.ZERO;
        BigDecimal famCost = familyPremium != null ? 
            familyPremium.multiply(BigDecimal.valueOf(dependentsEnrolled != null ? dependentsEnrolled : 0)) : 
            BigDecimal.ZERO;
        return empCost.add(famCost);
    }

    public void incrementEmployeeEnrollment() {
        this.employeesEnrolled = (this.employeesEnrolled != null ? this.employeesEnrolled : 0) + 1;
    }

    public void decrementEmployeeEnrollment() {
        if (this.employeesEnrolled != null && this.employeesEnrolled > 0) {
            this.employeesEnrolled--;
        }
    }

    public void incrementDependentEnrollment() {
        this.dependentsEnrolled = (this.dependentsEnrolled != null ? this.dependentsEnrolled : 0) + 1;
    }

    public void decrementDependentEnrollment() {
        if (this.dependentsEnrolled != null && this.dependentsEnrolled > 0) {
            this.dependentsEnrolled--;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PortfolioPlan that = (PortfolioPlan) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("PortfolioPlan{id=%s, employeesEnrolled=%d, dependentsEnrolled=%d}", 
                           id, employeesEnrolled, dependentsEnrolled);
    }
}
