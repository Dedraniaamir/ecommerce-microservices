package com.msproj.userservice.dto;

import com.msproj.userservice.entity.Address;
import com.msproj.userservice.entity.Admin;
import com.msproj.userservice.entity.Customer;
import com.msproj.userservice.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// REQUEST DTO with validation
public class CreateUserRequestDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String userType = "CUSTOMER"; // Default to customer

    // Address fields
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Admin-specific fields
    private String employeeId;
    private String department;

    // Default constructor
    public CreateUserRequestDto() {}

    // Builder pattern for complex object creation
    public static class Builder {
        private CreateUserRequestDto dto = new CreateUserRequestDto();

        public Builder username(String username) {
            dto.username = username;
            return this;
        }

        public Builder email(String email) {
            dto.email = email;
            return this;
        }

        public Builder password(String password) {
            dto.password = password;
            return this;
        }

        public Builder firstName(String firstName) {
            dto.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            dto.lastName = lastName;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            dto.phoneNumber = phoneNumber;
            return this;
        }

        public Builder dateOfBirth(LocalDate dateOfBirth) {
            dto.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder userType(String userType) {
            dto.userType = userType;
            return this;
        }

        public Builder address(String streetAddress, String city, String state, String postalCode, String country) {
            dto.streetAddress = streetAddress;
            dto.city = city;
            dto.state = state;
            dto.postalCode = postalCode;
            dto.country = country;
            return this;
        }

        public Builder adminDetails(String employeeId, String department) {
            dto.employeeId = employeeId;
            dto.department = department;
            dto.userType = "ADMIN";
            return this;
        }

        public CreateUserRequestDto build() {
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Method to convert DTO to Entity (Factory pattern)
    public User toEntity() {
        if ("ADMIN".equalsIgnoreCase(userType)) {
            Admin admin = new Admin(username, email, password, firstName, lastName, employeeId, department);
            return admin;
        } else {
            Customer customer = new Customer(username, email, password, firstName, lastName, phoneNumber);
            customer.setDateOfBirth(dateOfBirth);

            if (hasAddress()) {
                Address address = new Address(streetAddress, city, state, postalCode, country);
                customer.setAddress(address);
            }

            return customer;
        }
    }

    private boolean hasAddress() {
        return streetAddress != null && city != null && state != null;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

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

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
