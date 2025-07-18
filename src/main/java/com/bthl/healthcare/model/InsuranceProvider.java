/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/InsuranceProvider.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Insurance Provider entity model for managing insurance companies and their offerings
 * Description: I designed this InsuranceProvider entity to represent insurance companies that offer
 *              healthcare plans through my platform. I've included comprehensive provider information,
 *              financial ratings, service areas, and relationship management capabilities.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of InsuranceProvider entity with comprehensive provider management
 * 
 * Git Commit: git commit -m "feat: add InsuranceProvider entity model with ratings and service area management"
 * 
 * Next Dev Feature: Add provider performance analytics and competitive analysis capabilities
 * TODO: Implement provider API integration for real-time plan updates and claims processing
 */

package com.bthl.healthcare.model;

import com.bthl.healthcare.model.enums.ProviderType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * I created this InsuranceProvider entity to manage the insurance companies that
 * offer healthcare plans through my platform. I've designed it to capture all
 * essential provider information needed for broker relationships and plan management.
 */
@Entity
@Table(name = "insurance_providers", indexes = {
    @Index(name = "idx_providers_name", columnList = "name"),
    @Index(name = "idx_providers_code", columnList = "provider_code"),
    @Index(name = "idx_providers_type", columnList = "provider_type"),
    @Index(name = "idx_providers_active", columnList = "is_active")
})
@EntityListeners(AuditingEntityListener.class)
public class InsuranceProvider {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @NotBlank(message = "Provider name is required")
    @Size(max = 255, message = "Provider name must not exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    public String name;

    @Size(max = 255, message = "Legal name must not exceed 255 characters")
    @Column(name = "legal_name", length = 255)
    public String legalName;

    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "Provider code must be 2-10 uppercase letters and numbers")
    @Column(name = "provider_code", unique = true, length = 50)
    public String providerCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    public ProviderType providerType;

    @Pattern(regexp = "^[ABC][\\+\\-]?$", message = "AM Best rating must be A, B, or C optionally followed by + or -")
    @Column(name = "am_best_rating", length = 10)
    public String amBestRating;

    @Pattern(regexp = "^(AAA|AA|A|BBB|BB|B|CCC|CC|C|D)[\\+\\-]?$", message = "Financial strength rating must be valid credit rating format")
    @Column(name = "financial_strength_rating", length = 10)
    public String financialStrengthRating;

    @Size(max = 1000, message = "Headquarters address must not exceed 1000 characters")
    @Column(name = "headquarters_address", columnDefinition = "TEXT")
    public String headquartersAddress;

    @ElementCollection
    @CollectionTable(name = "provider_service_areas", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "service_area")
    public Set<String> serviceAreas = new HashSet<>();

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in valid international format")
    @Column(name = "phone", length = 20)
    public String phone;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", length = 255)
    public String email;

    @Pattern(regexp = "^https?://[\\w\\-\\.]+(\\.[a-zA-Z]{2,})+(/.*)?$", message = "Website URL must be valid")
    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    @Column(name = "website_url", length = 255)
    public String websiteUrl;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Customer service phone must be in valid international format")
    @Column(name = "customer_service_phone", length = 20)
    public String customerServicePhone;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Claims phone must be in valid international format")
    @Column(name = "claims_phone", length = 20)
    public String claimsPhone;

    @Min(value = 0, message = "Network size must be non-negative")
    @Max(value = 10000000, message = "Network size must not exceed 10 million")
    @Column(name = "network_size")
    public Integer networkSize;

    @Min(value = 1800, message = "Established year must be after 1800")
    @Max(value = 2030, message = "Established year must not be in the future")
    @Column(name = "established_year")
    public Integer establishedYear;

    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    @Column(name = "logo_url", length = 255)
    public String logoUrl;

    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true;

    @Column(name = "contracted_since")
    public LocalDate contractedSince;

    @Column(name = "contract_expires")
    public LocalDate contractExpires;

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

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<InsurancePlan> insurancePlans = new HashSet<>();

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    public Set<BrokerProviderRelationship> brokerRelationships = new HashSet<>();

    // I implement default constructor for JPA
    public InsuranceProvider() {}

    // I create a comprehensive constructor for provider creation
    public InsuranceProvider(String name, String providerCode, ProviderType providerType, 
                           String headquartersAddress, String email, String phone) {
        this.name = name;
        this.providerCode = providerCode;
        this.providerType = providerType;
        this.headquartersAddress = headquartersAddress;
        this.email = email;
        this.phone = phone;
        this.isActive = true;
    }

    // I create a simplified constructor for basic provider creation
    public InsuranceProvider(String name, ProviderType providerType, String email) {
        this(name, null, providerType, null, email, null);
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

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public String getAmBestRating() {
        return amBestRating;
    }

    public void setAmBestRating(String amBestRating) {
        this.amBestRating = amBestRating;
    }

    public String getFinancialStrengthRating() {
        return financialStrengthRating;
    }

    public void setFinancialStrengthRating(String financialStrengthRating) {
        this.financialStrengthRating = financialStrengthRating;
    }

    public String getHeadquartersAddress() {
        return headquartersAddress;
    }

    public void setHeadquartersAddress(String headquartersAddress) {
        this.headquartersAddress = headquartersAddress;
    }

    public Set<String> getServiceAreas() {
        return new HashSet<>(serviceAreas);
    }

    public void setServiceAreas(Set<String> serviceAreas) {
        this.serviceAreas = serviceAreas != null ? new HashSet<>(serviceAreas) : new HashSet<>();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getCustomerServicePhone() {
        return customerServicePhone;
    }

    public void setCustomerServicePhone(String customerServicePhone) {
        this.customerServicePhone = customerServicePhone;
    }

    public String getClaimsPhone() {
        return claimsPhone;
    }

    public void setClaimsPhone(String claimsPhone) {
        this.claimsPhone = claimsPhone;
    }

    public Integer getNetworkSize() {
        return networkSize;
    }

    public void setNetworkSize(Integer networkSize) {
        this.networkSize = networkSize;
    }

    public Integer getEstablishedYear() {
        return establishedYear;
    }

    public void setEstablishedYear(Integer establishedYear) {
        this.establishedYear = establishedYear;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getContractedSince() {
        return contractedSince;
    }

    public void setContractedSince(LocalDate contractedSince) {
        this.contractedSince = contractedSince;
    }

    public LocalDate getContractExpires() {
        return contractExpires;
    }

    public void setContractExpires(LocalDate contractExpires) {
        this.contractExpires = contractExpires;
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

    public Set<InsurancePlan> getInsurancePlans() {
        return new HashSet<>(insurancePlans);
    }

    public void setInsurancePlans(Set<InsurancePlan> insurancePlans) {
        this.insurancePlans = insurancePlans != null ? new HashSet<>(insurancePlans) : new HashSet<>();
    }

    public Set<BrokerProviderRelationship> getBrokerRelationships() {
        return new HashSet<>(brokerRelationships);
    }

    public void setBrokerRelationships(Set<BrokerProviderRelationship> brokerRelationships) {
        this.brokerRelationships = brokerRelationships != null ? new HashSet<>(brokerRelationships) : new HashSet<>();
    }

    // I create convenient computed properties for frontend use

    @JsonProperty("displayName")
    public String getDisplayName() {
        return legalName != null && !legalName.trim().isEmpty() ? legalName : name;
    }

    @JsonProperty("companyAge")
    public Integer getCompanyAge() {
        return establishedYear != null ? LocalDateTime.now().getYear() - establishedYear : null;
    }

    @JsonProperty("hasLogo")
    public boolean getHasLogo() {
        return logoUrl != null && !logoUrl.trim().isEmpty();
    }

    @JsonProperty("planCount")
    public int getPlanCount() {
        return insurancePlans != null ? (int) insurancePlans.stream().filter(plan -> plan.isActive).count() : 0;
    }

    @JsonProperty("brokerCount")
    public int getBrokerCount() {
        return brokerRelationships != null ? (int) brokerRelationships.stream().filter(rel -> rel.isActive).count() : 0;
    }

    @JsonProperty("serviceAreaCount")
    public int getServiceAreaCount() {
        return serviceAreas != null ? serviceAreas.size() : 0;
    }

    @JsonProperty("contractStatus")
    public String getContractStatus() {
        if (contractExpires == null) return "No Contract";
        LocalDate now = LocalDate.now();
        if (contractExpires.isBefore(now)) return "Expired";
        if (contractExpires.isBefore(now.plusDays(30))) return "Expiring Soon";
        return "Active";
    }

    @JsonProperty("networkSizeCategory")
    public String getNetworkSizeCategory() {
        if (networkSize == null) return "Unknown";
        if (networkSize < 1000) return "Small";
        if (networkSize < 10000) return "Medium";
        if (networkSize < 100000) return "Large";
        return "National";
    }

    @JsonProperty("ratingScore")
    public double getRatingScore() {
        // I calculate a composite rating score from AM Best and Financial Strength ratings
        double amBestScore = getAmBestScore();
        double financialScore = getFinancialStrengthScore();
        return (amBestScore + financialScore) / 2.0;
    }

    // I implement business logic methods for provider management

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean canOfferPlans() {
        return isActive && contractExpires != null && contractExpires.isAfter(LocalDate.now());
    }

    public void addServiceArea(String serviceArea) {
        if (serviceArea != null && !serviceArea.trim().isEmpty()) {
            if (serviceAreas == null) {
                serviceAreas = new HashSet<>();
            }
            serviceAreas.add(serviceArea.trim());
        }
    }

    public void removeServiceArea(String serviceArea) {
        if (serviceAreas != null) {
            serviceAreas.remove(serviceArea);
        }
    }

    public boolean servicesArea(String area) {
        return serviceAreas != null && serviceAreas.contains(area);
    }

    public void addPlan(InsurancePlan plan) {
        if (plan != null) {
            if (insurancePlans == null) {
                insurancePlans = new HashSet<>();
            }
            insurancePlans.add(plan);
            plan.provider = this;
        }
    }

    public void removePlan(InsurancePlan plan) {
        if (insurancePlans != null && plan != null) {
            insurancePlans.remove(plan);
            plan.provider = null;
        }
    }

    public Set<InsurancePlan> getActivePlans() {
        if (insurancePlans == null) return new HashSet<>();
        return insurancePlans.stream()
            .filter(plan -> plan.isActive)
            .collect(java.util.stream.Collectors.toSet());
    }

    public Set<InsurancePlan> getPlansByType(ProviderType type) {
        if (insurancePlans == null) return new HashSet<>();
        return insurancePlans.stream()
            .filter(plan -> plan.planType == type && plan.isActive)
            .collect(java.util.stream.Collectors.toSet());
    }

    public boolean hasCompleteProfile() {
        return name != null && !name.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               providerType != null &&
               headquartersAddress != null && !headquartersAddress.trim().isEmpty() &&
               phone != null && !phone.trim().isEmpty();
    }

    public String getIncompleteProfileFields() {
        List<String> missingFields = new ArrayList<>();
        
        if (name == null || name.trim().isEmpty()) missingFields.add("Provider Name");
        if (email == null || email.trim().isEmpty()) missingFields.add("Email");
        if (providerType == null) missingFields.add("Provider Type");
        if (headquartersAddress == null || headquartersAddress.trim().isEmpty()) missingFields.add("Headquarters Address");
        if (phone == null || phone.trim().isEmpty()) missingFields.add("Phone");
        if (amBestRating == null || amBestRating.trim().isEmpty()) missingFields.add("AM Best Rating");
        if (financialStrengthRating == null || financialStrengthRating.trim().isEmpty()) missingFields.add("Financial Strength Rating");
        
        return String.join(", ", missingFields);
    }

    // I implement rating calculation helper methods

    public double getAmBestScore() {
        if (amBestRating == null) return 0.0;
        String rating = amBestRating.substring(0, 1);
        double baseScore = switch (rating) {
            case "A" -> 9.0;
            case "B" -> 6.0;
            case "C" -> 3.0;
            default -> 0.0;
        };
        
        // I adjust for + or - modifiers
        if (amBestRating.contains("+")) baseScore += 0.5;
        if (amBestRating.contains("-")) baseScore -= 0.5;
        
        return Math.max(0.0, Math.min(10.0, baseScore));
    }

    public double getFinancialStrengthScore() {
        if (financialStrengthRating == null) return 0.0;
        
        double baseScore = switch (financialStrengthRating.substring(0, financialStrengthRating.length() > 2 ? 3 : financialStrengthRating.length())) {
            case "AAA" -> 10.0;
            case "AA" -> 9.0;
            case "A" -> 8.0;
            case "BBB" -> 7.0;
            case "BB" -> 6.0;
            case "B" -> 5.0;
            case "CCC" -> 4.0;
            case "CC" -> 3.0;
            case "C" -> 2.0;
            case "D" -> 1.0;
            default -> 0.0;
        };
        
        // I adjust for + or - modifiers
        if (financialStrengthRating.contains("+")) baseScore += 0.5;
        if (financialStrengthRating.contains("-")) baseScore -= 0.5;
        
        return Math.max(0.0, Math.min(10.0, baseScore));
    }

    // I implement equals and hashCode based on UUID and provider code
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InsuranceProvider provider = (InsuranceProvider) obj;
        
        // I first compare by ID if both have IDs
        if (id != null && provider.id != null) {
            return Objects.equals(id, provider.id);
        }
        
        // I fall back to provider code or name comparison for new entities
        if (providerCode != null && provider.providerCode != null) {
            return Objects.equals(providerCode, provider.providerCode);
        }
        
        return Objects.equals(name, provider.name);
    }

    @Override
    public int hashCode() {
        // I use ID if available, otherwise provider code, otherwise name
        if (id != null) return Objects.hash(id);
        if (providerCode != null) return Objects.hash(providerCode);
        return Objects.hash(name);
    }

    // I provide a comprehensive toString method for debugging
    @Override
    public String toString() {
        return "InsuranceProvider{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", providerCode='" + providerCode + '\'' +
               ", providerType=" + providerType +
               ", amBestRating='" + amBestRating + '\'' +
               ", financialStrengthRating='" + financialStrengthRating + '\'' +
               ", isActive=" + isActive +
               ", planCount=" + (insurancePlans != null ? insurancePlans.size() : 0) +
               ", serviceAreaCount=" + (serviceAreas != null ? serviceAreas.size() : 0) +
               ", createdAt=" + createdAt +
               '}';
    }
}
