/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/Company.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Company entity model for healthcare portfolio management and client organizations
 * Description: I designed this Company entity to represent organizations that purchase healthcare
 *              coverage for their employees. I've included comprehensive business information,
 *              contact details, and portfolio management capabilities.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of Company entity with business profile and portfolio management
 * 
 * Git Commit: git commit -m "feat: add Company entity model for healthcare portfolio management"
 * 
 * Next Dev Feature: Add company hierarchy support for multi-subsidiary organizations
 * TODO: Implement company analytics dashboard and employee demographic tracking
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
import java.time.LocalDateTime;
import java.util.*;

/**
 * I created this Company entity to manage client organizations that purchase
 * healthcare coverage through my platform. I've designed it to capture all
 * essential business information needed for insurance underwriting and portfolio management.
 */
@Entity
@Table(name = "companies", indexes = {
    @Index(name = "idx_companies_name", columnList = "name"),
    @Index(name = "idx_companies_tax_id", columnList = "tax_id"),
    @Index(name = "idx_companies_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class Company {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    public String name;

    @Size(max = 255, message = "Legal name must not exceed 255 characters")
    @Column(name = "legal_name", length = 255)
    public String legalName;

    @Pattern(regexp = "^\\d{2}-\\d{7}$", message = "Tax ID must be in format XX-XXXXXXX")
    @Column(name = "tax_id", length = 50)
    public String taxId;

    @Size(max = 100, message = "Industry must not exceed 100 characters")
    @Column(name = "industry", length = 100)
    public String industry;

    @Min(value = 1, message = "Employee count must be at least 1")
    @Max(value = 1000000, message = "Employee count must not exceed 1,000,000")
    @Column(name = "employee_count")
    public Integer employeeCount;

    @Size(max = 1000, message = "Headquarters address must not exceed 1000 characters")
    @Column(name = "headquarters_address", columnDefinition = "TEXT")
    public String headquartersAddress;

    @Size(max = 1000, message = "Billing address must not exceed 1000 characters")
    @Column(name = "billing_address", columnDefinition = "TEXT")
    public String billingAddress;

    @Email(message = "Primary contact email must be valid")
    @Size(max = 255, message = "Primary contact email must not exceed 255 characters")
    @Column(name = "primary_contact_email", length = 255)
    public String primaryContactEmail;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Primary contact phone must be in valid international format")
    @Column(name = "primary_contact_phone", length = 20)
    public String primaryContactPhone;

    @Pattern(regexp = "^https?://[\\w\\-\\.]+(\\.[a-zA-Z]{2,})+(/.*)?$", message = "Website URL must be valid")
    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    @Column(name = "website_url", length = 255)
    public String websiteUrl;

    @DecimalMin(value = "0.0", message = "Annual revenue must be non-negative")
    @DecimalMax(value = "999999999999999.99", message = "Annual revenue must not exceed 999 trillion")
    @Column(name = "annual_revenue", precision = 15, scale = 2)
    public BigDecimal annualRevenue;

    @Min(value = 1800, message = "Founded year must be after 1800")
    @Max(value = 2030, message = "Founded year must not be in the future")
    @Column(name = "founded_year")
    public Integer foundedYear;

    @Pattern(regexp = "^(ACTIVE|INACTIVE|PROSPECT|TERMINATED)$", message = "Status must be ACTIVE, INACTIVE, PROSPECT, or TERMINATED")
    @Column(name = "status", length = 20)
    public String status = "PROSPECT";

    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    @Column(name = "logo_url", length = 255)
    public String logoUrl;

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

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<CompanyHealthcarePortfolio> healthcarePortfolios = new HashSet<>();

    // I implement default constructor for JPA
    public Company() {}

    // I create a comprehensive constructor for company creation
    public Company(String name, String legalName, String taxId, String industry, 
                   Integer employeeCount, String headquartersAddress, String primaryContactEmail) {
        this.name = name;
        this.legalName = legalName;
        this.taxId = taxId;
        this.industry = industry;
        this.employeeCount = employeeCount;
        this.headquartersAddress = headquartersAddress;
        this.primaryContactEmail = primaryContactEmail;
        this.status = "PROSPECT";
    }

    // I create a simplified constructor for basic company creation
    public Company(String name, String primaryContactEmail, Integer employeeCount) {
        this(name, null, null, null, employeeCount, null, primaryContactEmail);
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

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public String getHeadquartersAddress() {
        return headquartersAddress;
    }

    public void setHeadquartersAddress(String headquartersAddress) {
        this.headquartersAddress = headquartersAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }

    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }

    public String getPrimaryContactPhone() {
        return primaryContactPhone;
    }

    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public BigDecimal getAnnualRevenue() {
        return annualRevenue;
    }

    public void setAnnualRevenue(BigDecimal annualRevenue) {
        this.annualRevenue = annualRevenue;
    }

    public Integer getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
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

    public Set<CompanyHealthcarePortfolio> getHealthcarePortfolios() {
        return new HashSet<>(healthcarePortfolios);
    }

    public void setHealthcarePortfolios(Set<CompanyHealthcarePortfolio> healthcarePortfolios) {
        this.healthcarePortfolios = healthcarePortfolios != null ? new HashSet<>(healthcarePortfolios) : new HashSet<>();
    }

    // I create convenient computed properties for frontend use

    @JsonProperty("displayName")
    public String getDisplayName() {
        return legalName != null && !legalName.trim().isEmpty() ? legalName : name;
    }

    @JsonProperty("companyAge")
    public Integer getCompanyAge() {
        return foundedYear != null ? LocalDateTime.now().getYear() - foundedYear : null;
    }

    @JsonProperty("employeeSizeCategory")
    public String getEmployeeSizeCategory() {
        if (employeeCount == null) return "Unknown";
        if (employeeCount < 10) return "Startup";
        if (employeeCount < 50) return "Small";
        if (employeeCount < 250) return "Medium";
        if (employeeCount < 1000) return "Large";
        return "Enterprise";
    }

    @JsonProperty("revenueCategory")
    public String getRevenueCategory() {
        if (annualRevenue == null) return "Not Disclosed";
        BigDecimal million = new BigDecimal("1000000");
        BigDecimal billion = new BigDecimal("1000000000");
        
        if (annualRevenue.compareTo(million) < 0) return "Under $1M";
        if (annualRevenue.compareTo(million.multiply(new BigDecimal("10"))) < 0) return "$1M - $10M";
        if (annualRevenue.compareTo(million.multiply(new BigDecimal("100"))) < 0) return "$10M - $100M";
        if (annualRevenue.compareTo(billion) < 0) return "$100M - $1B";
        return "Over $1B";
    }

    @JsonProperty("isActive")
    public boolean getIsActive() {
        return "ACTIVE".equals(status);
    }

    @JsonProperty("isProspect")
    public boolean getIsProspect() {
        return "PROSPECT".equals(status);
    }

    @JsonProperty("hasLogo")
    public boolean getHasLogo() {
        return logoUrl != null && !logoUrl.trim().isEmpty();
    }

    @JsonProperty("portfolioCount")
    public int getPortfolioCount() {
        return healthcarePortfolios != null ? healthcarePortfolios.size() : 0;
    }

    @JsonProperty("statusClass")
    public String getStatusClass() {
        return switch (status != null ? status : "PROSPECT") {
            case "ACTIVE" -> "status-active";
            case "INACTIVE" -> "status-inactive";
            case "PROSPECT" -> "status-prospect";
            case "TERMINATED" -> "status-terminated";
            default -> "status-unknown";
        };
    }

    // I implement business logic methods for company management

    public void activate() {
        this.status = "ACTIVE";
    }

    public void deactivate() {
        this.status = "INACTIVE";
    }

    public void terminate() {
        this.status = "TERMINATED";
    }

    public void markAsProspect() {
        this.status = "PROSPECT";
    }

    public boolean canPurchaseInsurance() {
        return "ACTIVE".equals(status) || "PROSPECT".equals(status);
    }

    public boolean hasCompleteProfile() {
        return name != null && !name.trim().isEmpty() &&
               primaryContactEmail != null && !primaryContactEmail.trim().isEmpty() &&
               employeeCount != null && employeeCount > 0 &&
               headquartersAddress != null && !headquartersAddress.trim().isEmpty();
    }

    public String getIncompleteProfileFields() {
        List<String> missingFields = new ArrayList<>();
        
        if (name == null || name.trim().isEmpty()) missingFields.add("Company Name");
        if (primaryContactEmail == null || primaryContactEmail.trim().isEmpty()) missingFields.add("Primary Contact Email");
        if (employeeCount == null || employeeCount <= 0) missingFields.add("Employee Count");
        if (headquartersAddress == null || headquartersAddress.trim().isEmpty()) missingFields.add("Headquarters Address");
        if (industry == null || industry.trim().isEmpty()) missingFields.add("Industry");
        
        return String.join(", ", missingFields);
    }

    public void addPortfolio(CompanyHealthcarePortfolio portfolio) {
        if (portfolio != null) {
            if (healthcarePortfolios == null) {
                healthcarePortfolios = new HashSet<>();
            }
            healthcarePortfolios.add(portfolio);
            portfolio.company = this;
        }
    }

    public void removePortfolio(CompanyHealthcarePortfolio portfolio) {
        if (healthcarePortfolios != null && portfolio != null) {
            healthcarePortfolios.remove(portfolio);
            portfolio.company = null;
        }
    }

    public CompanyHealthcarePortfolio getCurrentPortfolio() {
        if (healthcarePortfolios == null || healthcarePortfolios.isEmpty()) {
            return null;
        }
        
        return healthcarePortfolios.stream()
            .filter(portfolio -> "ACTIVE".equals(portfolio.status))
            .max(Comparator.comparing(portfolio -> portfolio.effectiveDate))
            .orElse(null);
    }

    public BigDecimal getTotalPremiumSpend() {
        if (healthcarePortfolios == null) return BigDecimal.ZERO;
        
        return healthcarePortfolios.stream()
            .filter(portfolio -> "ACTIVE".equals(portfolio.status))
            .map(portfolio -> portfolio.totalPremium != null ? portfolio.totalPremium : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // I implement equals and hashCode based on UUID and tax ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Company company = (Company) obj;
        
        // I first compare by ID if both have IDs
        if (id != null && company.id != null) {
            return Objects.equals(id, company.id);
        }
        
        // I fall back to tax ID or name comparison for new entities
        if (taxId != null && company.taxId != null) {
            return Objects.equals(taxId, company.taxId);
        }
        
        return Objects.equals(name, company.name);
    }

    @Override
    public int hashCode() {
        // I use ID if available, otherwise tax ID, otherwise name
        if (id != null) return Objects.hash(id);
        if (taxId != null) return Objects.hash(taxId);
        return Objects.hash(name);
    }

    // I provide a comprehensive toString method for debugging
    @Override
    public String toString() {
        return "Company{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", legalName='" + legalName + '\'' +
               ", taxId='" + taxId + '\'' +
               ", industry='" + industry + '\'' +
               ", employeeCount=" + employeeCount +
               ", status='" + status + '\'' +
               ", portfolioCount=" + (healthcarePortfolios != null ? healthcarePortfolios.size() : 0) +
               ", createdAt=" + createdAt +
               '}';
    }
}
