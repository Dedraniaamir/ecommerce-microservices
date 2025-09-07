package com.msproj.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable; /**
 * Embedded Address classes
 */
@Embeddable
public class ShippingAddress {
    @Column(name = "ship_to_name")
    private String name;

    @Column(name = "ship_street_address")
    private String streetAddress;

    @Column(name = "ship_city")
    private String city;

    @Column(name = "ship_state")
    private String state;

    @Column(name = "ship_postal_code")
    private String postalCode;

    @Column(name = "ship_country")
    private String country;

    @Column(name = "ship_phone")
    private String phone;

    // Constructors
    public ShippingAddress() {}

    public ShippingAddress(String name, String streetAddress, String city,
                           String state, String postalCode, String country) {
        this.name = name;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    // Getters and Setters
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

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
