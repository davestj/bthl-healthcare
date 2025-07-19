/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/enums/ProviderType.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Provider type enumeration for insurance plan categorization
 * Description: I designed this enumeration to categorize different types of insurance coverage
 *              offered through my healthcare platform, enabling proper plan organization and filtering.
 * 
 * Changelog:
 * 2025-07-18: Separated PlanTier enum into individual file to resolve Java compilation errors
 * 2025-07-16: Initial creation of ProviderType enum with healthcare coverage categories
 * 
 * Git Commit: git commit -m "refactor: separate ProviderType enum into individual file to resolve compilation errors"
 * 
 * Next Dev Feature: Add provider type-specific validation rules and premium calculations
 * TODO: Implement type-specific compliance requirements and regulatory tracking
 */

package com.bthl.healthcare.model.enums;

/**
 * I created this ProviderType enumeration to categorize the different types of
 * insurance coverage available through my healthcare platform. Each type represents
 * a distinct category of coverage with specific regulations and requirements.
 */
public enum ProviderType {
    
    /**
     * I use HEALTH_INSURANCE for comprehensive medical coverage including
     * doctor visits, hospital stays, prescriptions, and preventive care
     */
    HEALTH_INSURANCE("Health Insurance", "Comprehensive medical coverage including hospital, physician, and prescription benefits",
                     "fas fa-heartbeat", "#e74c3c"),
    
    /**
     * I use DENTAL for dental care coverage including preventive, basic,
     * and major dental procedures
     */
    DENTAL("Dental Insurance", "Dental care coverage for preventive, basic, and major dental procedures",
           "fas fa-tooth", "#3498db"),
    
    /**
     * I use VISION for eye care coverage including exams, glasses,
     * and contact lenses
     */
    VISION("Vision Insurance", "Eye care coverage including exams, glasses, contacts, and vision correction",
           "fas fa-eye", "#2ecc71"),
    
    /**
     * I use LIFE_INSURANCE for life insurance benefits providing
     * financial protection for beneficiaries
     */
    LIFE_INSURANCE("Life Insurance", "Life insurance coverage providing financial protection for beneficiaries",
                   "fas fa-shield-alt", "#9b59b6"),
    
    /**
     * I use DISABILITY for disability insurance providing income
     * replacement during periods of disability
     */
    DISABILITY("Disability Insurance", "Disability coverage providing income replacement during inability to work",
               "fas fa-wheelchair", "#f39c12");

    public final String displayName;
    public final String description;
    public final String iconClass;
    public final String colorCode;

    ProviderType(String displayName, String description, String iconClass, String colorCode) {
        this.displayName = displayName;
        this.description = description;
        this.iconClass = iconClass;
        this.colorCode = colorCode;
    }

    /**
     * I provide this method to check if the provider type requires medical underwriting
     * 
     * @return true if medical underwriting is required, false otherwise
     */
    public boolean requiresMedicalUnderwriting() {
        return this == HEALTH_INSURANCE || this == LIFE_INSURANCE || this == DISABILITY;
    }

    /**
     * I provide this method to check if the provider type has age restrictions
     * 
     * @return true if age restrictions apply, false otherwise
     */
    public boolean hasAgeRestrictions() {
        return this == LIFE_INSURANCE || this == DISABILITY;
    }

    /**
     * I provide this method to check if the provider type supports family coverage
     * 
     * @return true if family coverage is available, false otherwise
     */
    public boolean supportsFamilyCoverage() {
        return this == HEALTH_INSURANCE || this == DENTAL || this == VISION;
    }

    /**
     * I provide this method to get the regulatory compliance category for the provider type
     * 
     * @return compliance category for regulatory purposes
     */
    public String getComplianceCategory() {
        return switch (this) {
            case HEALTH_INSURANCE -> "ACA_COMPLIANT";
            case DENTAL, VISION -> "SUPPLEMENTAL";
            case LIFE_INSURANCE, DISABILITY -> "VOLUNTARY";
        };
    }

    /**
     * I provide this method to get the CSS class for provider type styling
     * 
     * @return CSS class name for frontend styling
     */
    public String getCssClass() {
        return switch (this) {
            case HEALTH_INSURANCE -> "provider-health";
            case DENTAL -> "provider-dental";
            case VISION -> "provider-vision";
            case LIFE_INSURANCE -> "provider-life";
            case DISABILITY -> "provider-disability";
        };
    }
}
