package com.msproj.userservice.dto;

import com.msproj.userservice.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

// RESPONSE DTO - What we send back to clients
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private UserStatus status;
    private String userType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Customer-specific fields
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Integer loyaltyPoints;
    private CustomerTier customerTier;
    private String formattedAddress;

    // Admin-specific fields
    private String employeeId;
    private String department;
    private AdminLevel adminLevel;

    // Static factory methods (Factory Pattern)
    public static UserResponseDto fromUser(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.firstName = user.getFirstName();
        dto.lastName = user.getLastName();
        dto.fullName = user.getFullName();
        dto.status = user.getStatus();
        dto.userType = user.getUserType();
        dto.createdAt = user.getCreatedAt();
        dto.updatedAt = user.getUpdatedAt();

        // POLYMORPHISM - Check actual type and cast
        if (user instanceof Customer customer) {
            dto.phoneNumber = customer.getPhoneNumber();
            dto.dateOfBirth = customer.getDateOfBirth();
            dto.loyaltyPoints = customer.getLoyaltyPoints();
            dto.customerTier = customer.getCustomerTier();
            if (customer.getAddress() != null) {
                dto.formattedAddress = customer.getAddress().getFormattedAddress();
            }
        } else if (user instanceof Admin admin) {
            dto.employeeId = admin.getEmployeeId();
            dto.department = admin.getDepartment();
            dto.adminLevel = admin.getAdminLevel();
        }

        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public CustomerTier getCustomerTier() { return customerTier; }
    public void setCustomerTier(CustomerTier customerTier) { this.customerTier = customerTier; }

    public String getFormattedAddress() { return formattedAddress; }
    public void setFormattedAddress(String formattedAddress) { this.formattedAddress = formattedAddress; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public AdminLevel getAdminLevel() { return adminLevel; }
    public void setAdminLevel(AdminLevel adminLevel) { this.adminLevel = adminLevel; }
}
