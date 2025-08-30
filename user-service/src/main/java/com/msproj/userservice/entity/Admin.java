package com.msproj.userservice.entity;

import jakarta.persistence.*; /**
 * Admin Entity demonstrating INHERITANCE and different behavior
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {  // INHERITANCE

    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @Column(name = "department")
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "admin_level")
    private AdminLevel adminLevel = AdminLevel.JUNIOR;

    // Constructors
    public Admin() {
        super();
    }

    public Admin(String username, String email, String password,
                 String firstName, String lastName, String employeeId, String department) {
        super(username, email, password, firstName, lastName);
        this.employeeId = employeeId;
        this.department = department;
    }

    // POLYMORPHISM - Override parent method with different behavior
    @Override
    public String getUserType() {
        return "ADMIN";
    }

    // Admin-specific business methods
    public boolean canManageUsers() {
        return AdminLevel.SENIOR.equals(adminLevel) || AdminLevel.SUPER_ADMIN.equals(adminLevel);
    }

    public boolean canAccessReports() {
        return !AdminLevel.JUNIOR.equals(adminLevel);
    }

    public void promote() {
        switch (adminLevel) {
            case JUNIOR -> adminLevel = AdminLevel.SENIOR;
            case SENIOR -> adminLevel = AdminLevel.SUPER_ADMIN;
            case SUPER_ADMIN -> { /* Already at highest level */ }
        }
    }

    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public AdminLevel getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(AdminLevel adminLevel) {
        this.adminLevel = adminLevel;
    }
}
