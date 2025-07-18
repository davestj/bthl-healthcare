/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/InsuranceBroker.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Insurance Broker entity model for managing licensed insurance brokers
 * Description: I designed this InsuranceBroker entity to represent licensed insurance professionals
 *              who facilitate insurance plan selection and enrollment for companies and individuals.
 *              I've included licensing information, specializations, and performance tracking.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of InsuranceBroker entity with licensing and specialization management
 * 
 * Git Commit: git commit -m "feat: add InsuranceBroker entity model with licensing and territory management"
 * 
 * Next Dev Feature: Add broker performance analytics and commission tracking dashboard
 * TODO: Implement automated license renewal reminders and compliance monitoring
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
 * I created this InsuranceBroker entity to manage licensed insurance professionals
 * who work through my healthcare platform. I've designed it to track licensing,
 * specializations, territories, and performance metrics for comprehensive broker management.
 */
@Entity
@Table(name = "insurance_brokers", indexes = {
    @Index(name = "idx_brokers_user", columnList = "user_id"),
    @Index(name = "idx_brokers_license", columnList = "license_number"),
    @Index(name = "idx_brokers_state", columnList = "license_state"),
    @Index(name = "idx_brokers_active", columnList = "is_active")
})
@EntityListeners(AuditingEntityListener.class)
public class InsuranceBroker {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    public User user;

    @NotBlank(message = "License number is required")
    @Size(max = 100, message = "License number must not exceed 100 characters")
    @Column(name = "license_number", unique = true, nullable = false, length = 100)
    public String licenseNumber;

    @NotBlank(message = "License state is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "License state must be a valid 2-letter state code")
    @Column(name = "license_state", nullable = false, length = 2)
    public String licenseState;

    @NotNull(message = "License expiration date is required")
    @Future(message = "License expiration date must be in the future")
    @Column(name = "license_expires", nullable = false)
    public LocalDate licenseExpires;

    @Size(max = 255, message = "Agency name must not exceed 255 characters")
    @Column(name = "agency_name", length = 255)
    public String agencyName;

    @Size(max = 1000, message = "Agency address must not exceed 1000 characters")
    @Column(name = "agency_address", columnDefinition = "TEXT")
    public String agencyAddress;

    @ElementCollection
    @CollectionTable(name = "broker_specializations", joinColumns = @JoinColumn(name = "broker_id"))
    @Column(name = "specialization")
    public Set<String> specializations = new HashSet<>();

    @Min(value = 0, message = "Years of experience must be non-negative")
    @Max(value = 50, message = "Years of experience must not exceed 50")
    @Column(name = "years_experience")
    public Integer yearsExperience;

    @DecimalMin(value = "0.0000", message = "Commission rate must be non-negative")
    @DecimalMax(value = "1.0000", message = "Commission rate must not exceed 100%")
    @Column(name = "commission_rate", precision = 5, scale = 4)
    public BigDecimal commissionRate;

    @ElementCollection
    @CollectionTable(name = "broker_territories", joinColumns = @JoinColumn(name = "broker_id"))
    @Column(name = "territory")
    public Set<String> territories = new HashSet<>();

    @Column(name = "certifications", columnDefinition = "jsonb")
    public Map<String, Object> certifications = new HashMap<>();

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    @Column(name = "bio", columnDefinition = "TEXT")
    public String bio;

    @Pattern(regexp = "^https://[\\w\\.-]+\\.linkedin\\.com/in/[\\w\\-]+/?$", message = "LinkedIn URL must be valid")
    @Size(max = 255, message = "LinkedIn URL must not exceed 255 characters")
    @Column(name = "linkedin_url", length = 255)
    public String linkedinUrl;

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

    @OneToMany(mappedBy = "broker", fetch = FetchType.LAZY)
    public Set<CompanyHealthcarePortfolio> managedPortfolios = new HashSet<>();

    @OneToMany(mappedBy = "broker", fetch = FetchType.LAZY)
    public Set<BrokerProviderRelationship> providerRelationships = new HashSet<>();

    // I implement default constructor for JPA
    public InsuranceBroker() {}

    // I create a comprehensive constructor for broker creation
    public InsuranceBroker(User user, String licenseNumber, String licenseState, 
                          LocalDate licenseExpires, String agencyName, Integer yearsExperience) {
        this.user = user;
        this.licenseNumber = licenseNumber;
        this.licenseState = licenseState;
        this.licenseExpires = licenseExpires;
        this.agencyName = agencyName;
        this.yearsExperience = yearsExperience;
        this.isActive = true;
    }

    // I create a simplified constructor for basic broker creation
    public InsuranceBroker(User user, String licenseNumber, String licenseState, LocalDate licenseExpires) {
        this(user, licenseNumber, licenseState, licenseExpires, null, null);
    }

    // I implement getter and setter methods following my naming conventions

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseState() {
        return licenseState;
    }

    public void setLicenseState(String licenseState) {
        this.licenseState = licenseState;
    }

    public LocalDate getLicenseExpires() {
        return licenseExpires;
    }

    public void setLicenseExpires(LocalDate licenseExpires) {
        this.licenseExpires = licenseExpires;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyAddress() {
        return agencyAddress;
    }

    public void setAgencyAddress(String agencyAddress) {
        this.agencyAddress = agencyAddress;
    }

    public Set<String> getSpecializations() {
        return new HashSet<>(specializations);
    }

    public void setSpecializations(Set<String> specializations) {
        this.specializations = specializations != null ? new HashSet<>(specializations) : new HashSet<>();
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Set<String> getTerritories() {
        return new HashSet<>(territories);
    }

    public void setTerritories(Set<String> territories) {
        this.territories = territories != null ? new HashSet<>(territories) : new HashSet<>();
    }

    public Map<String, Object> getCertifications() {
        return new HashMap<>(certifications);
    }

    public void setCertifications(Map<String, Object> certifications) {
        this.certifications = certifications != null ? new HashMap<>(certifications) : new HashMap<>();
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
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

    public Set<CompanyHealthcarePortfolio> getManagedPortfolios() {
        return new HashSet<>(managedPortfolios);
    }

    public void setManagedPortfolios(Set<CompanyHealthcarePortfolio> managedPortfolios) {
        this.managedPortfolios = managedPortfolios != null ? new HashSet<>(managedPortfolios) : new HashSet<>();
    }

    public Set<BrokerProviderRelationship> getProviderRelationships() {
        return new HashSet<>(providerRelationships);
    }

    public void setProviderRelationships(Set<BrokerProviderRelationship> providerRelationships) {
        this.providerRelationships = providerRelationships != null ? new HashSet<>(providerRelationships) : new HashSet<>();
    }

    // I create convenient computed properties for frontend use

    @JsonProperty("displayName")
    public String getDisplayName() {
        return user != null ? user.getFullName() : "Unknown Broker";
    }

    @JsonProperty("agencyDisplayName")
    public String getAgencyDisplayName() {
        return agencyName != null && !agencyName.trim().isEmpty() ? agencyName : "Independent Broker";
    }

    @JsonProperty("experienceLevel")
    public String getExperienceLevel() {
        if (yearsExperience == null) return "Unknown";
        if (yearsExperience < 2) return "New";
        if (yearsExperience < 5) return "Experienced";
        if (yearsExperience < 10) return "Senior";
        if (yearsExperience < 20) return "Expert";
        return "Master";
    }

    @JsonProperty("licenseStatus")
    public String getLicenseStatus() {
        if (licenseExpires == null) return "Unknown";
        LocalDate now = LocalDate.now();
        if (licenseExpires.isBefore(now)) return "Expired";
        if (licenseExpires.isBefore(now.plusDays(30))) return "Expiring Soon";
        if (licenseExpires.isBefore(now.plusDays(90))) return "Renewal Due";
        return "Active";
    }

    @JsonProperty("daysUntilLicenseExpires")
    public Long getDaysUntilLicenseExpires() {
        return licenseExpires != null ? java.time.ChronoUnit.DAYS.between(LocalDate.now(), licenseExpires) : null;
    }

    @JsonProperty("portfolioCount")
    public int getPortfolioCount() {
        return managedPortfolios != null ? (int) managedPortfolios.stream()
            .filter(portfolio -> "ACTIVE".equals(portfolio.status))
            .count() : 0;
    }

    @JsonProperty("providerCount")
    public int getProviderCount() {
        return providerRelationships != null ? (int) providerRelationships.stream()
            .filter(rel -> rel.isActive)
            .count() : 0;
    }

    @JsonProperty("specializationCount")
    public int getSpecializationCount() {
        return specializations != null ? specializations.size() : 0;
    }

    @JsonProperty("territoryCount")
    public int getTerritoryCount() {
        return territories != null ? territories.size() : 0;
    }

    @JsonProperty("certificationCount")
    public int getCertificationCount() {
        return certifications != null ? certifications.size() : 0;
    }

    @JsonProperty("hasLinkedIn")
    public boolean getHasLinkedIn() {
        return linkedinUrl != null && !linkedinUrl.trim().isEmpty();
    }

    @JsonProperty("hasCompleteProfile")
    public boolean getHasCompleteProfile() {
        return hasCompleteProfile();
    }

    // I implement business logic methods for broker management

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean canManagePortfolios() {
        return isActive && isLicenseValid() && user != null && user.isEnabled();
    }

    public boolean isLicenseValid() {
        return licenseExpires != null && licenseExpires.isAfter(LocalDate.now());
    }

    public boolean isLicenseExpiringSoon(int daysThreshold) {
        return licenseExpires != null && 
               licenseExpires.isAfter(LocalDate.now()) &&
               licenseExpires.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public void addSpecialization(String specialization) {
        if (specialization != null && !specialization.trim().isEmpty()) {
            if (specializations == null) {
                specializations = new HashSet<>();
            }
            specializations.add(specialization.trim());
        }
    }

    public void removeSpecialization(String specialization) {
        if (specializations != null) {
            specializations.remove(specialization);
        }
    }

    public boolean hasSpecialization(String specialization) {
        return specializations != null && specializations.contains(specialization);
    }

    public void addTerritory(String territory) {
        if (territory != null && !territory.trim().isEmpty()) {
            if (territories == null) {
                territories = new HashSet<>();
            }
            territories.add(territory.trim());
        }
    }

    public void removeTerritory(String territory) {
        if (territories != null) {
            territories.remove(territory);
        }
    }

    public boolean servesTerritory(String territory) {
        return territories != null && territories.contains(territory);
    }

    public void addCertification(String name, Object details) {
        if (name != null && !name.trim().isEmpty()) {
            if (certifications == null) {
                certifications = new HashMap<>();
            }
            certifications.put(name, details);
        }
    }

    public void removeCertification(String name) {
        if (certifications != null) {
            certifications.remove(name);
        }
    }

    public boolean hasCertification(String name) {
        return certifications != null && certifications.containsKey(name);
    }

    public void addPortfolio(CompanyHealthcarePortfolio portfolio) {
        if (portfolio != null) {
            if (managedPortfolios == null) {
                managedPortfolios = new HashSet<>();
            }
            managedPortfolios.add(portfolio);
            portfolio.broker = this;
        }
    }

    public void removePortfolio(CompanyHealthcarePortfolio portfolio) {
        if (managedPortfolios != null && portfolio != null) {
            managedPortfolios.remove(portfolio);
            portfolio.broker = null;
        }
    }

    public Set<CompanyHealthcarePortfolio> getActivePortfolios() {
        if (managedPortfolios == null) return new HashSet<>();
        return managedPortfolios.stream()
            .filter(portfolio -> "ACTIVE".equals(portfolio.status))
            .collect(java.util.stream.Collectors.toSet());
    }

    public BigDecimal getTotalManagedPremium() {
        if (managedPortfolios == null) return BigDecimal.ZERO;
        
        return managedPortfolios.stream()
            .filter(portfolio -> "ACTIVE".equals(portfolio.status))
            .map(portfolio -> portfolio.totalPremium != null ? portfolio.totalPremium : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getEstimatedAnnualCommission() {
        BigDecimal totalPremium = getTotalManagedPremium();
        BigDecimal rate = commissionRate != null ? commissionRate : BigDecimal.ZERO;
        return totalPremium.multiply(rate);
    }

    public boolean hasCompleteProfile() {
        return user != null &&
               licenseNumber != null && !licenseNumber.trim().isEmpty() &&
               licenseState != null && !licenseState.trim().isEmpty() &&
               licenseExpires != null &&
               yearsExperience != null &&
               agencyName != null && !agencyName.trim().isEmpty() &&
               bio != null && !bio.trim().isEmpty() &&
               specializations != null && !specializations.isEmpty() &&
               territories != null && !territories.isEmpty();
    }

    public String getIncompleteProfileFields() {
        List<String> missingFields = new ArrayList<>();
        
        if (user == null) missingFields.add("User Account");
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) missingFields.add("License Number");
        if (licenseState == null || licenseState.trim().isEmpty()) missingFields.add("License State");
        if (licenseExpires == null) missingFields.add("License Expiration");
        if (yearsExperience == null) missingFields.add("Years of Experience");
        if (agencyName == null || agencyName.trim().isEmpty()) missingFields.add("Agency Name");
        if (bio == null || bio.trim().isEmpty()) missingFields.add("Professional Bio");
        if (specializations == null || specializations.isEmpty()) missingFields.add("Specializations");
        if (territories == null || territories.isEmpty()) missingFields.add("Service Territories");
        if (commissionRate == null) missingFields.add("Commission Rate");
        
        return String.join(", ", missingFields);
    }

    // I implement equals and hashCode based on UUID and license number
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InsuranceBroker broker = (InsuranceBroker) obj;
        
        // I first compare by ID if both have IDs
        if (id != null && broker.id != null) {
            return Objects.equals(id, broker.id);
        }
        
        // I fall back to license number comparison for new entities
        return Objects.equals(licenseNumber, broker.licenseNumber);
    }

    @Override
    public int hashCode() {
        // I use ID if available, otherwise license number
        return id != null ? Objects.hash(id) : Objects.hash(licenseNumber);
    }

    // I provide a comprehensive toString method for debugging
    @Override
    public String toString() {
        return "InsuranceBroker{" +
               "id=" + id +
               ", user=" + (user != null ? user.getDisplayName() : "null") +
               ", licenseNumber='" + licenseNumber + '\'' +
               ", licenseState='" + licenseState + '\'' +
               ", licenseExpires=" + licenseExpires +
               ", agencyName='" + agencyName + '\'' +
               ", yearsExperience=" + yearsExperience +
               ", isActive=" + isActive +
               ", portfolioCount=" + (managedPortfolios != null ? managedPortfolios.size() : 0) +
               ", specializationCount=" + (specializations != null ? specializations.size() : 0) +
               ", createdAt=" + createdAt +
               '}';
    }
}
