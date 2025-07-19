package com.bthl.healthcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "broker_provider_relationships")
public class BrokerProviderRelationship {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "broker_id", nullable = false)
    private InsuranceBroker broker;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", nullable = false)
    private InsuranceProvider provider;
    
    @Column(name = "relationship_type", length = 50)
    private String relationshipType = "CONTRACTED";
    
    @Column(name = "commission_rate", precision = 5, scale = 4)
    private BigDecimal commissionRate;
    
    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;
    
    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public BrokerProviderRelationship() {}
    
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
}
