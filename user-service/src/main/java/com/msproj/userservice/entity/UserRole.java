package com.msproj.userservice.entity;

import jakarta.persistence.*;

import java.util.Objects; /**
 * UserRole Entity - Junction table for Many-to-Many relationship
 * Demonstrates COMPOSITION and proper JPA relationship mapping
 */
@Entity
@Table(name = "user_roles")
public class UserRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)  // Lazy loading for performance
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER) // Eager loading as we usually need role info
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Constructors
    public UserRole() {
        super();
    }

    public UserRole(User user, Role role) {
        super();
        this.user = user;
        this.role = role;
        this.isActive = true;
    }

    // Business methods
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(user.getId(), userRole.user.getId()) &&
                Objects.equals(role.getId(), userRole.role.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), role.getId());
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + getId() +
                ", userId=" + (user != null ? user.getId() : null) +
                ", roleName=" + (role != null ? role.getName() : null) +
                ", isActive=" + isActive +
                '}';
    }
}
