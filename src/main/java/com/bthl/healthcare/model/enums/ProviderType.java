/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/enums/ProviderType.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Provider type enumeration for insurance plan categorization
 * Description: I designed this enumeration to categorize different types of insurance coverage
 *              offered through my healthcare platform, enabling proper plan organization and filtering.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of ProviderType enum with healthcare coverage categories
 * 
 * Git Commit: git commit -m "feat: add ProviderType enum for insurance coverage categorization"
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

/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/enums/PlanTier.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Plan tier enumeration for healthcare plan metal levels and categorization
 * Description: I designed this enumeration to represent the standard ACA metal tier system
 *              for healthcare plans, enabling proper cost-sharing and coverage comparisons.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of PlanTier enum with ACA metal tier classifications
 * 
 * Git Commit: git commit -m "feat: add PlanTier enum for ACA metal tier classifications"
 * 
 * Next Dev Feature: Add tier-specific cost calculators and coverage comparison tools
 * TODO: Implement tier-based recommendation engine for optimal plan selection
 */

package com.bthl.healthcare.model.enums;

/**
 * I created this PlanTier enumeration to represent the standard ACA metal tier
 * system for healthcare plans. Each tier represents a different level of cost-sharing
 * between the insurance company and the covered individual.
 */
public enum PlanTier {
    
    /**
     * I use BRONZE for plans that typically cover 60% of healthcare costs
     * with lower premiums but higher deductibles and out-of-pocket costs
     */
    BRONZE("Bronze", "Lower premium, higher deductible plans covering approximately 60% of healthcare costs",
           60, 40, "#cd7f32", "bronze-tier"),
    
    /**
     * I use SILVER for plans that typically cover 70% of healthcare costs
     * with moderate premiums and cost-sharing
     */
    SILVER("Silver", "Moderate premium and cost-sharing plans covering approximately 70% of healthcare costs",
           70, 30, "#c0c0c0", "silver-tier"),
    
    /**
     * I use GOLD for plans that typically cover 80% of healthcare costs
     * with higher premiums but lower deductibles and out-of-pocket costs
     */
    GOLD("Gold", "Higher premium, lower deductible plans covering approximately 80% of healthcare costs",
         80, 20, "#ffd700", "gold-tier"),
    
    /**
     * I use PLATINUM for plans that typically cover 90% of healthcare costs
     * with the highest premiums but lowest out-of-pocket costs
     */
    PLATINUM("Platinum", "Highest premium, lowest out-of-pocket plans covering approximately 90% of healthcare costs",
             90, 10, "#e5e4e2", "platinum-tier"),
    
    /**
     * I use CATASTROPHIC for high-deductible plans available to young adults
     * and those with hardship exemptions, covering essential health benefits after deductible
     */
    CATASTROPHIC("Catastrophic", "High-deductible plans for young adults covering essential benefits after deductible",
                 60, 40, "#8b4513", "catastrophic-tier");

    public final String displayName;
    public final String description;
    public final int averageCoverage;
    public final int averageOutOfPocket;
    public final String colorCode;
    public final String cssClass;

    PlanTier(String displayName, String description, int averageCoverage, 
             int averageOutOfPocket, String colorCode, String cssClass) {
        this.displayName = displayName;
        this.description = description;
        this.averageCoverage = averageCoverage;
        this.averageOutOfPocket = averageOutOfPocket;
        this.colorCode = colorCode;
        this.cssClass = cssClass;
    }

    /**
     * I provide this method to check if the tier is suitable for young, healthy individuals
     * 
     * @return true if suitable for young, healthy individuals, false otherwise
     */
    public boolean isSuitableForYoungHealthy() {
        return this == BRONZE || this == CATASTROPHIC;
    }

    /**
     * I provide this method to check if the tier provides good value for frequent healthcare users
     * 
     * @return true if good for frequent users, false otherwise
     */
    public boolean isGoodForFrequentUsers() {
        return this == GOLD || this == PLATINUM;
    }

    /**
     * I provide this method to check if the tier qualifies for premium tax credits
     * 
     * @return true if eligible for tax credits, false otherwise
     */
    public boolean qualifiesForTaxCredits() {
        return this != CATASTROPHIC; // I exclude catastrophic plans from tax credits
    }

    /**
     * I provide this method to check if the tier has age restrictions
     * 
     * @return true if age restrictions apply, false otherwise
     */
    public boolean hasAgeRestrictions() {
        return this == CATASTROPHIC; // I restrict catastrophic plans to under 30 or hardship exemption
    }

    /**
     * I provide this method to get the relative premium cost compared to other tiers
     * 
     * @return relative premium cost (1-5 scale, 1 = lowest, 5 = highest)
     */
    public int getRelativePremiumCost() {
        return switch (this) {
            case CATASTROPHIC -> 1;
            case BRONZE -> 2;
            case SILVER -> 3;
            case GOLD -> 4;
            case PLATINUM -> 5;
        };
    }

    /**
     * I provide this method to get the relative out-of-pocket cost when using healthcare
     * 
     * @return relative out-of-pocket cost (1-5 scale, 1 = lowest, 5 = highest)
     */
    public int getRelativeOutOfPocketCost() {
        return switch (this) {
            case PLATINUM -> 1;
            case GOLD -> 2;
            case SILVER -> 3;
            case BRONZE -> 4;
            case CATASTROPHIC -> 5;
        };
    }

    /**
     * I provide this method to get the recommended income range for the tier
     * 
     * @return income range description for the tier
     */
    public String getRecommendedIncomeRange() {
        return switch (this) {
            case CATASTROPHIC -> "Low income, young adults";
            case BRONZE -> "Lower to moderate income";
            case SILVER -> "Moderate income, eligible for cost-sharing reductions";
            case GOLD -> "Higher income, frequent healthcare use";
            case PLATINUM -> "High income, chronic conditions or frequent care";
        };
    }

    /**
     * I provide this method to get the icon class for tier display
     * 
     * @return icon class name for frontend display
     */
    public String getIconClass() {
        return switch (this) {
            case BRONZE -> "fas fa-medal bronze-medal";
            case SILVER -> "fas fa-medal silver-medal";
            case GOLD -> "fas fa-medal gold-medal";
            case PLATINUM -> "fas fa-crown platinum-crown";
            case CATASTROPHIC -> "fas fa-shield-alt catastrophic-shield";
        };
    }

    /**
     * I provide this method to compare tiers by coverage level
     * 
     * @param other the tier to compare to
     * @return negative if this tier has lower coverage, positive if higher, zero if equal
     */
    public int compareByCoverage(PlanTier other) {
        return Integer.compare(this.averageCoverage, other.averageCoverage);
    }

    /**
     * I provide this method to get the next higher tier
     * 
     * @return the next higher tier, or null if this is the highest
     */
    public PlanTier getNextHigherTier() {
        return switch (this) {
            case CATASTROPHIC, BRONZE -> SILVER;
            case SILVER -> GOLD;
            case GOLD -> PLATINUM;
            case PLATINUM -> null;
        };
    }

    /**
     * I provide this method to get the next lower tier
     * 
     * @return the next lower tier, or null if this is the lowest
     */
    public PlanTier getNextLowerTier() {
        return switch (this) {
            case PLATINUM -> GOLD;
            case GOLD -> SILVER;
            case SILVER -> BRONZE;
            case BRONZE, CATASTROPHIC -> null;
        };
    }
}
