package com.msproj.userservice.entity;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Role Entity demonstrating JPA relationships and OOP principles
 */
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {  // INHERITANCE

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private RoleName name;

    @Column(name = "description")
    private String description;

    // Constructors
    public Role() {
        super();
    }

    public Role(RoleName name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    // Business methods
    public boolean isAdminRole() {
        return RoleName.ADMIN.equals(this.name);
    }

    public boolean isCustomerRole() {
        return RoleName.CUSTOMER.equals(this.name);
    }

    // Getters and Setters
    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return name == role.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + getId() +
                ", name=" + name +
                ", description='" + description + '\'' +
                '}';
    }
}

