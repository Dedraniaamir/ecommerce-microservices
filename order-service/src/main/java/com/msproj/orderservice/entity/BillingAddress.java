package com.msproj.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BillingAddress {
    @Column(name = "bill_to_name")
    private String name;

    @Column(name = "bill_street_address")
    private String streetAddress;

    @Column(name = "bill_city")
    private String city;

    @Column(name = "bill_state")
    private String state;

    @Column(name = "bill_postal_code")
    private String postalCode;

    @Column(name = "bill_country")
    private String country;

    // Constructors
    public BillingAddress() {}

    public BillingAddress(String name, String streetAddress, String city,
                          String state, String postalCode, String country) {
        this.name = name;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    // Getters and Setters (same pattern as ShippingAddress)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
