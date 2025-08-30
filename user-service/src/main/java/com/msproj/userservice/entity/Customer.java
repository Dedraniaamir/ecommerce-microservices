package com.msproj.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * Customer Entity demonstrating INHERITANCE and POLYMORPHISM
 *
 * OOP Concepts:
 * 1. INHERITANCE - Extends User class
 * 2. POLYMORPHISM - Can be treated as User
 * 3. SPECIALIZATION - Adds customer-specific fields and behavior
 */
@Entity
@DiscriminatorValue("CUSTOMER")  // Value in discriminator column
public class Customer extends User {  // INHERITANCE

    @Column(name = "phone_number")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_tier")
    private CustomerTier customerTier = CustomerTier.BRONZE;

    // Address as embedded object (COMPOSITION)
    @Embedded
    private Address address;

    // Constructors
    public Customer() {
        super();
    }

    public Customer(String username, String email, String password,
                    String firstName, String lastName, String phoneNumber) {
        super(username, email, password, firstName, lastName);  // Call parent constructor
        this.phoneNumber = phoneNumber;
        this.loyaltyPoints = 0;
        this.customerTier = CustomerTier.BRONZE;
    }

    // POLYMORPHISM - Override parent method
    @Override
    public String getUserType() {
        return "CUSTOMER";
    }

    // Business methods specific to Customer
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        updateCustomerTier();
    }

    public void redeemLoyaltyPoints(int points) {
        if (this.loyaltyPoints >= points) {
            this.loyaltyPoints -= points;
            updateCustomerTier();
        } else {
            throw new IllegalArgumentException("Insufficient loyalty points");
        }
    }

    private void updateCustomerTier() {
        if (loyaltyPoints >= 10000) {
            this.customerTier = CustomerTier.PLATINUM;
        } else if (loyaltyPoints >= 5000) {
            this.customerTier = CustomerTier.GOLD;
        } else if (loyaltyPoints >= 1000) {
            this.customerTier = CustomerTier.SILVER;
        } else {
            this.customerTier = CustomerTier.BRONZE;
        }
    }

    public double getDiscountPercentage() {
        return customerTier.getDiscountPercentage();
    }

    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
        updateCustomerTier();
    }

    public CustomerTier getCustomerTier() {
        return customerTier;
    }

    public void setCustomerTier(CustomerTier customerTier) {
        this.customerTier = customerTier;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}

