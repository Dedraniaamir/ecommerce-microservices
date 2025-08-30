package com.msproj.userservice.entity;

/**
 * Customer Tier Enum with business logic
 */
public enum CustomerTier {
    BRONZE(0.0),
    SILVER(5.0),
    GOLD(10.0),
    PLATINUM(15.0);

    private final double discountPercentage;

    CustomerTier(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
}
