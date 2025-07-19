/**
 * File: /Users/dstjohn/dev/02_davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/model/enums/PlanTier.java
 * Author: davestj (David St John)
 * Date: 2025-07-18
 * Purpose: Plan tier enumeration for healthcare plan metal levels and categorization
 * Description: I designed this enumeration to represent the standard ACA metal tier system
 *              for healthcare plans, enabling proper cost-sharing and coverage comparisons.
 * 
 * Changelog:
 * 2025-07-18: Separated from ProviderType.java file to resolve Java compilation errors
 * 2025-07-16: Initial creation of PlanTier enum with ACA metal tier classifications
 * 
 * Git Commit: git commit -m "refactor: separate PlanTier enum into individual file to resolve compilation errors"
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
    
    BRONZE("Bronze", "Lower premium, higher deductible plans covering approximately 60% of healthcare costs",
           60, 40, "#cd7f32", "bronze-tier"),
    
    SILVER("Silver", "Moderate premium and cost-sharing plans covering approximately 70% of healthcare costs",
           70, 30, "#c0c0c0", "silver-tier"),
    
    GOLD("Gold", "Higher premium, lower deductible plans covering approximately 80% of healthcare costs",
         80, 20, "#ffd700", "gold-tier"),
    
    PLATINUM("Platinum", "Highest premium, lowest out-of-pocket plans covering approximately 90% of healthcare costs",
             90, 10, "#e5e4e2", "platinum-tier"),
    
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

    public boolean isSuitableForYoungHealthy() {
        return this == BRONZE || this == CATASTROPHIC;
    }

    public boolean isGoodForFrequentUsers() {
        return this == GOLD || this == PLATINUM;
    }

    public boolean qualifiesForTaxCredits() {
        return this != CATASTROPHIC;
    }

    public boolean hasAgeRestrictions() {
        return this == CATASTROPHIC;
    }

    public int getRelativePremiumCost() {
        return switch (this) {
            case CATASTROPHIC -> 1;
            case BRONZE -> 2;
            case SILVER -> 3;
            case GOLD -> 4;
            case PLATINUM -> 5;
        };
    }

    public int getRelativeOutOfPocketCost() {
        return switch (this) {
            case PLATINUM -> 1;
            case GOLD -> 2;
            case SILVER -> 3;
            case BRONZE -> 4;
            case CATASTROPHIC -> 5;
        };
    }

    public String getRecommendedIncomeRange() {
        return switch (this) {
            case CATASTROPHIC -> "Low income, young adults";
            case BRONZE -> "Lower to moderate income";
            case SILVER -> "Moderate income, eligible for cost-sharing reductions";
            case GOLD -> "Higher income, frequent healthcare use";
            case PLATINUM -> "High income, chronic conditions or frequent care";
        };
    }

    public String getIconClass() {
        return switch (this) {
            case BRONZE -> "fas fa-medal bronze-medal";
            case SILVER -> "fas fa-medal silver-medal";
            case GOLD -> "fas fa-medal gold-medal";
            case PLATINUM -> "fas fa-crown platinum-crown";
            case CATASTROPHIC -> "fas fa-shield-alt catastrophic-shield";
        };
    }

    public int compareByCoverage(PlanTier other) {
        return Integer.compare(this.averageCoverage, other.averageCoverage);
    }

    public PlanTier getNextHigherTier() {
        return switch (this) {
            case CATASTROPHIC, BRONZE -> SILVER;
            case SILVER -> GOLD;
            case GOLD -> PLATINUM;
            case PLATINUM -> null;
        };
    }

    public PlanTier getNextLowerTier() {
        return switch (this) {
            case PLATINUM -> GOLD;
            case GOLD -> SILVER;
            case SILVER -> BRONZE;
            case BRONZE, CATASTROPHIC -> null;
        };
    }
}
