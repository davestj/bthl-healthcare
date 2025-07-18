/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/InsurancePlan.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Insurance Plan entity model for managing healthcare insurance plans and coverage details
 * Description: I designed this InsurancePlan entity to represent individual healthcare plans offered by
 *              insurance providers. I've included comprehensive coverage details, cost-sharing information,
 *              and benefit structures that enable detailed plan comparison and selection.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of InsurancePlan entity with comprehensive coverage and cost-sharing details
 * 
 * Git Commit: git commit -m "feat: add InsurancePlan entity model with detailed coverage and cost-sharing structure"
 * 
 * Next Dev Feature: Add plan comparison algorithms and recommendation engine integration
 * TODO: Implement real-time premium calculation API and network provider integration
 */

package com.bthl.healthcare.model;

import com.bthl.healthcare.model.enums.ProviderType;
import com.bthl.healthcare.model.enums.PlanTier;
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
 * I created this InsurancePlan entity to represent individual healthcare plans
 * with comprehensive coverage details and cost-sharing structures. I've designed
 * it to support detailed plan comparisons and enable informed decision-making.
 */
@Entity
@Table(name = "insurance_plans", indexes = {
    @Index(name = "idx_plans_provider", columnList = "provider_id"),
    @Index(name = "idx_plans_code", columnList = "plan_code"),
    @Index(name = "idx_plans_type", columnList = "plan_type"),
    @Index(name = "idx_plans_tier", columnList = "tier"),
    @Index(name = "idx_plans_active", columnList = "is_active")
})
@EntityListeners(AuditingEntityListener.class)
public class InsurancePlan {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", nullable = false)
    public InsuranceProvider provider;

    @NotBlank(message = "Plan name is required")
    @Size(max = 255, message = "Plan name must not exceed 255 characters")
    @Column(name = "plan_name", nullable = false, length = 255)
    public String planName;

    @Pattern(regexp = "^[A-Z0-9\\-_]{2,20}$", message = "Plan code must be 2-20 uppercase letters, numbers, hyphens, or underscores")
    @Column(name = "plan_code", unique = true, length = 100)
    public String planCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    public ProviderType planType;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier")
    public PlanTier tier;

    @Pattern(regexp = "^(HMO|PPO|EPO|POS|HDHP|INDEMNITY)$", message = "Network type must be HMO, PPO, EPO, POS, HDHP, or INDEMNITY")
    @Column(name = "network_type", length = 50)
    public String networkType;

    @DecimalMin(value = "0.00", message = "Deductible must be non-negative")
    @DecimalMax(value = "999999.99", message = "Deductible must not exceed $999,999.99")
    @Column(name = "deductible", precision = 10, scale = 2)
    public BigDecimal deductible;

    @DecimalMin(value = "0.00", message = "Out-of-pocket maximum must be non-negative")
    @DecimalMax(value = "999999.99", message = "Out-of-pocket maximum must not exceed $999,999.99")
    @Column(name = "out_of_pocket_max", precision = 10, scale = 2)
    public BigDecimal outOfPocketMax;

    @DecimalMin(value = "0.00", message = "Primary care copay must be non-negative")
    @DecimalMax(value = "9999.99", message = "Primary care copay must not exceed $9,999.99")
    @Column(name = "copay_primary_care", precision = 8, scale = 2)
    public BigDecimal copayPrimaryCare;

    @DecimalMin(value = "0.00", message = "Specialist copay must be non-negative")
    @DecimalMax(value = "9999.99", message = "Specialist copay must not exceed $9,999.99")
    @Column(name = "copay_specialist", precision = 8, scale = 2)
    public BigDecimal copaySpecialist;

    @DecimalMin(value = "0.00", message = "Emergency copay must be non-negative")
    @DecimalMax(value = "9999.99", message = "Emergency copay must not exceed $9,999.99")
    @Column(name = "copay_emergency", precision = 8, scale = 2)
    public BigDecimal copayEmergency;

    @DecimalMin(value = "0.00", message = "Coinsurance percentage must be non-negative")
    @DecimalMax(value = "100.00", message = "Coinsurance percentage must not exceed 100%")
    @Column(name = "coinsurance_percentage", precision = 5, scale = 2)
    public BigDecimal coinsurancePercentage;

    @Column(name = "prescription_coverage", columnDefinition = "jsonb")
    public Map<String, Object> prescriptionCoverage = new HashMap<>();

    @Column(name = "mental_health_coverage", nullable = false)
    public Boolean mentalHealthCoverage = true;

    @Column(name = "maternity_coverage", nullable = false)
    public Boolean maternityCoverage = true;

    @Column(name = "preventive_care_coverage", nullable = false)
    public Boolean preventiveCareCoverage = true;

    @Column(name = "coverage_details", columnDefinition = "jsonb")
    public Map<String, Object> coverageDetails = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "plan_exclusions", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "exclusion")
    public Set<String> exclusions = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "plan_geographic_coverage", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "coverage_area")
    public Set<String> geographicCoverage = new HashSet<>();

    @Column(name = "effective_date")
    public LocalDate effectiveDate;

    @Column(name = "termination_date")
    public LocalDate terminationDate;

    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true;

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

    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY)
    public Set<PortfolioPlan> portfolioPlans = new HashSet<>();

    // I implement default constructor for JPA
    public InsurancePlan() {}

    // I create a comprehensive constructor for plan creation
    public InsurancePlan(InsuranceProvider provider, String planName, String planCode, 
                        ProviderType planType, PlanTier tier, String networkType,
                        BigDecimal deductible, BigDecimal outOfPocketMax) {
        this.provider = provider;
        this.planName = planName;
        this.planCode = planCode;
        this.planType = planType;
        this.tier = tier;
        this.networkType = networkType;
        this.deductible = deductible;
        this.outOfPocketMax = outOfPocketMax;
        this.isActive = true;
        this.mentalHealthCoverage = true;
        this.maternityCoverage = true;
        this.preventiveCareCoverage = true;
    }

    // I create a simplified constructor for basic plan creation
    public InsurancePlan(InsuranceProvider provider, String planName, ProviderType planType, PlanTier tier) {
        this(provider, planName, null, planType, tier, null, null, null);
    }

    // I implement getter and setter methods following my naming conventions

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public InsuranceProvider getProvider() {
        return provider;
    }

    public void setProvider(InsuranceProvider provider) {
        this.provider = provider;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public ProviderType getPlanType() {
        return planType;
    }

    public void setPlanType(ProviderType planType) {
        this.planType = planType;
    }

    public PlanTier getTier() {
        return tier;
    }

    public void setTier(PlanTier tier) {
        this.tier = tier;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public BigDecimal getDeductible() {
        return deductible;
    }

    public void setDeductible(BigDecimal deductible) {
        this.deductible = deductible;
    }

    public BigDecimal getOutOfPocketMax() {
        return outOfPocketMax;
    }

    public void setOutOfPocketMax(BigDecimal outOfPocketMax) {
        this.outOfPocketMax = outOfPocketMax;
    }

    public BigDecimal getCopayPrimaryCare() {
        return copayPrimaryCare;
    }

    public void setCopayPrimaryCare(BigDecimal copayPrimaryCare) {
        this.copayPrimaryCare = copayPrimaryCare;
    }

    public BigDecimal getCopaySpecialist() {
        return copaySpecialist;
    }

    public void setCopaySpecialist(BigDecimal copaySpecialist) {
        this.copaySpecialist = copaySpecialist;
    }

    public BigDecimal getCopayEmergency() {
        return copayEmergency;
    }

    public void setCopayEmergency(BigDecimal copayEmergency) {
        this.copayEmergency = copayEmergency;
    }

    public BigDecimal getCoinsurancePercentage() {
        return coinsurancePercentage;
    }

    public void setCoinsurancePercentage(BigDecimal coinsurancePercentage) {
        this.coinsurancePercentage = coinsurancePercentage;
    }

    public Map<String, Object> getPrescriptionCoverage() {
        return new HashMap<>(prescriptionCoverage);
    }

    public void setPrescriptionCoverage(Map<String, Object> prescriptionCoverage) {
        this.prescriptionCoverage = prescriptionCoverage != null ? new HashMap<>(prescriptionCoverage) : new HashMap<>();
    }

    public Boolean getMentalHealthCoverage() {
        return mentalHealthCoverage;
    }

    public void setMentalHealthCoverage(Boolean mentalHealthCoverage) {
        this.mentalHealthCoverage = mentalHealthCoverage;
    }

    public Boolean getMaternityCoverage() {
        return maternityCoverage;
    }

    public void setMaternityCoverage(Boolean maternityCoverage) {
        this.maternityCoverage = maternityCoverage;
    }

    public Boolean getPreventiveCareCoverage() {
        return preventiveCareCoverage;
    }

    public void setPreventiveCareCoverage(Boolean preventiveCareCoverage) {
        this.preventiveCareCoverage = preventiveCareCoverage;
    }

    public Map<String, Object> getCoverageDetails() {
        return new HashMap<>(coverageDetails);
    }

    public void setCoverageDetails(Map<String, Object> coverageDetails) {
        this.coverageDetails = coverageDetails != null ? new HashMap<>(coverageDetails) : new HashMap<>();
    }

    public Set<String> getExclusions() {
        return new HashSet<>(exclusions);
    }

    public void setExclusions(Set<String> exclusions) {
        this.exclusions = exclusions != null ? new HashSet<>(exclusions) : new HashSet<>();
    }

    public Set<String> getGeographicCoverage() {
        return new HashSet<>(geographicCoverage);
    }

    public void setGeographicCoverage(Set<String> geographicCoverage) {
        this.geographicCoverage = geographicCoverage != null ? new HashSet<>(geographicCoverage) : new HashSet<>();
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        String providerName = provider != null ? provider.getName() : "Unknown Provider";
        return providerName + " - " + planName;
    }

    @JsonProperty("tierDisplayName")
    public String getTierDisplayName() {
        return tier != null ? tier.displayName : "No Tier";
    }

    @JsonProperty("typeDisplayName")
    public String getTypeDisplayName() {
        return planType != null ? planType.displayName : "Unknown Type";
    }

    @JsonProperty("isCurrentlyActive")
    public boolean getIsCurrentlyActive() {
        LocalDate now = LocalDate.now();
        return isActive && 
               (effectiveDate == null || !effectiveDate.isAfter(now)) &&
               (terminationDate == null || !terminationDate.isBefore(now));
    }

    @JsonProperty("coverageScore")
    public double getCoverageScore() {
        // I calculate a coverage score based on benefits and cost-sharing
        double score = 0.0;
        
        // I add points for comprehensive coverage
        if (mentalHealthCoverage) score += 10;
        if (maternityCoverage) score += 10;
        if (preventiveCareCoverage) score += 15;
        
        // I adjust score based on cost-sharing (lower is better)
        if (deductible != null) {
            if (deductible.compareTo(new BigDecimal("1000")) <= 0) score += 15;
            else if (deductible.compareTo(new BigDecimal("5000")) <= 0) score += 10;
            else score += 5;
        }
        
        if (outOfPocketMax != null) {
            if (outOfPocketMax.compareTo(new BigDecimal("5000")) <= 0) score += 15;
            else if (outOfPocketMax.compareTo(new BigDecimal("10000")) <= 0) score += 10;
            else score += 5;
        }
        
        // I add points for low copays
        if (copayPrimaryCare != null && copayPrimaryCare.compareTo(new BigDecimal("30")) <= 0) score += 10;
        if (copaySpecialist != null && copaySpecialist.compareTo(new BigDecimal("50")) <= 0) score += 10;
        
        // I add points for prescription coverage
        if (prescriptionCoverage != null && !prescriptionCoverage.isEmpty()) score += 15;
        
        return Math.min(100.0, score);
    }

    @JsonProperty("affordabilityScore")
    public double getAffordabilityScore() {
        // I calculate affordability based on cost-sharing amounts (inverse scoring)
        double score = 100.0;
        
        // I deduct points for higher deductibles
        if (deductible != null) {
            double deductiblePoints = Math.min(30.0, deductible.doubleValue() / 1000.0 * 5.0);
            score -= deductiblePoints;
        }
        
        // I deduct points for higher out-of-pocket maximums
        if (outOfPocketMax != null) {
            double oopPoints = Math.min(25.0, outOfPocketMax.doubleValue() / 1000.0 * 2.5);
            score -= oopPoints;
        }
        
        // I deduct points for higher copays
        if (copayPrimaryCare != null) {
            score -= Math.min(15.0, copayPrimaryCare.doubleValue() / 10.0 * 3.0);
        }
        
        if (copaySpecialist != null) {
            score -= Math.min(15.0, copaySpecialist.doubleValue() / 10.0 * 2.0);
        }
        
        return Math.max(0.0, score);
    }

    @JsonProperty("hasComprehensiveCoverage")
    public boolean getHasComprehensiveCoverage() {
        return mentalHealthCoverage && maternityCoverage && preventiveCareCoverage &&
               prescriptionCoverage != null && !prescriptionCoverage.isEmpty();
    }

    @JsonProperty("exclusionCount")
    public int getExclusionCount() {
        return exclusions != null ? exclusions.size() : 0;
    }

    @JsonProperty("coverageAreaCount")
    public int getCoverageAreaCount() {
        return geographicCoverage != null ? geographicCoverage.size() : 0;
    }

    // I implement business logic methods for plan management

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void terminate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
        if (terminationDate != null && terminationDate.isBefore(LocalDate.now())) {
            this.isActive = false;
        }
    }

    public boolean isAvailableForEnrollment() {
        return getIsCurrentlyActive() && provider != null && provider.getIsActive();
    }

    public boolean coversGeographicArea(String area) {
        return geographicCoverage != null && geographicCoverage.contains(area);
    }

    public void addGeographicCoverage(String area) {
        if (area != null && !area.trim().isEmpty()) {
            if (geographicCoverage == null) {
                geographicCoverage = new HashSet<>();
            }
            geographicCoverage.add(area.trim());
        }
    }

    public void removeGeographicCoverage(String area) {
        if (geographicCoverage != null) {
            geographicCoverage.remove(area);
        }
    }

    public void addExclusion(String exclusion) {
        if (exclusion != null && !exclusion.trim().isEmpty()) {
            if (exclusions == null) {
                exclusions = new HashSet<>();
            }
            exclusions.add(exclusion.trim());
        }
    }

    public void removeExclusion(String exclusion) {
        if (exclusions != null) {
            exclusions.remove(exclusion);
        }
    }

    public boolean hasExclusion(String exclusion) {
        return exclusions != null && exclusions.contains(exclusion);
    }

    public void updatePrescriptionCoverage(String tier, Object coverage) {
        if (tier != null && !tier.trim().isEmpty()) {
            if (prescriptionCoverage == null) {
                prescriptionCoverage = new HashMap<>();
            }
            prescriptionCoverage.put(tier, coverage);
        }
    }

    public void updateCoverageDetail(String benefit, Object details) {
        if (benefit != null && !benefit.trim().isEmpty()) {
            if (coverageDetails == null) {
                coverageDetails = new HashMap<>();
            }
            coverageDetails.put(benefit, details);
        }
    }

    public BigDecimal getEstimatedAnnualCost(BigDecimal estimatedUsage) {
        // I calculate estimated annual cost based on plan structure and usage
        BigDecimal totalCost = BigDecimal.ZERO;
        
        // I add deductible if usage exceeds it
        if (deductible != null && estimatedUsage != null && estimatedUsage.compareTo(deductible) > 0) {
            totalCost = totalCost.add(deductible);
            BigDecimal remainingUsage = estimatedUsage.subtract(deductible);
            
            // I apply coinsurance to remaining usage
            if (coinsurancePercentage != null) {
                BigDecimal coinsuranceCost = remainingUsage.multiply(coinsurancePercentage.divide(new BigDecimal("100")));
                totalCost = totalCost.add(coinsuranceCost);
            }
        } else if (estimatedUsage != null) {
            totalCost = totalCost.add(estimatedUsage);
        }
        
        // I cap at out-of-pocket maximum
        if (outOfPocketMax != null && totalCost.compareTo(outOfPocketMax) > 0) {
            totalCost = outOfPocketMax;
        }
        
        return totalCost;
    }

    public boolean hasCompleteInformation() {
        return planName != null && !planName.trim().isEmpty() &&
               planType != null &&
               networkType != null && !networkType.trim().isEmpty() &&
               deductible != null &&
               outOfPocketMax != null &&
               provider != null;
    }

    public String getIncompleteInformationFields() {
        List<String> missingFields = new ArrayList<>();
        
        if (planName == null || planName.trim().isEmpty()) missingFields.add("Plan Name");
        if (planType == null) missingFields.add("Plan Type");
        if (networkType == null || networkType.trim().isEmpty()) missingFields.add("Network Type");
        if (deductible == null) missingFields.add("Deductible");
        if (outOfPocketMax == null) missingFields.add("Out-of-Pocket Maximum");
        if (provider == null) missingFields.add("Insurance Provider");
        if (tier == null) missingFields.add("Plan Tier");
        if (copayPrimaryCare == null) missingFields.add("Primary Care Copay");
        if (copaySpecialist == null) missingFields.add("Specialist Copay");
        if (geographicCoverage == null || geographicCoverage.isEmpty()) missingFields.add("Geographic Coverage");
        
        return String.join(", ", missingFields);
    }

    // I implement equals and hashCode based on UUID and plan code
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InsurancePlan plan = (InsurancePlan) obj;
        
        // I first compare by ID if both have IDs
        if (id != null && plan.id != null) {
            return Objects.equals(id, plan.id);
        }
        
        // I fall back to plan code comparison for new entities
        if (planCode != null && plan.planCode != null) {
            return Objects.equals(planCode, plan.planCode);
        }
        
        return Objects.equals(planName, plan.planName) && Objects.equals(provider, plan.provider);
    }

    @Override
    public int hashCode() {
        // I use ID if available, otherwise plan code, otherwise name and provider
        if (id != null) return Objects.hash(id);
        if (planCode != null) return Objects.hash(planCode);
        return Objects.hash(planName, provider);
    }

    // I provide a comprehensive toString method for debugging
    @Override
    public String toString() {
        return "InsurancePlan{" +
               "id=" + id +
               ", planName='" + planName + '\'' +
               ", planCode='" + planCode + '\'' +
               ", planType=" + planType +
               ", tier=" + tier +
               ", networkType='" + networkType + '\'' +
               ", deductible=" + deductible +
               ", outOfPocketMax=" + outOfPocketMax +
               ", provider=" + (provider != null ? provider.getName() : "null") +
               ", isActive=" + isActive +
               ", createdAt=" + createdAt +
               '}';
    }
}
