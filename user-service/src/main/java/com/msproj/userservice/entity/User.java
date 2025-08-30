package com.msproj.userservice.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity demonstrating OOP Inheritance and JPA Relationships
 *
 * OOP Concepts Demonstrated:
 * 1. INHERITANCE - Extends BaseEntity
 * 2. POLYMORPHISM - Can be treated as BaseEntity
 * 3. ENCAPSULATION - Private fields with validation
 * 4. COMPOSITION - Contains Role objects
 *
 * JPA Concepts:
 * 1. @Entity - Marks as JPA entity
 * 2. @Table - Custom table configuration
 * 3. @OneToMany - Relationship mapping
 * 4. @Enumerated - Enum mapping
 * 5. Bean Validation annotations
 */
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "username")
        })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // All subclasses in one table
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User extends BaseEntity {  // INHERITANCE from BaseEntity

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)  // Store enum as string in DB
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    // COMPOSITION: User HAS roles (not IS roles)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> roles = new HashSet<>();

    // Default constructor for JPA
    public User() {
        super();  // Call parent constructor
    }

    // Business constructor
    public User(String username, String email, String password, String firstName, String lastName) {
        super();  // INHERITANCE - call parent constructor
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = UserStatus.ACTIVE;
    }

    // Business methods demonstrating ENCAPSULATION and behavior
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status);
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }

    // Helper method for role management
    public void addRole(Role role) {
        UserRole userRole = new UserRole(this, role);
        this.roles.add(userRole);
    }

    public boolean hasRole(Role role) {
        return roles.stream()
                .anyMatch(userRole -> userRole.getRole().equals(role));
    }

    // POLYMORPHISM - This method can be overridden by subclasses
    public String getUserType() {
        return "BASIC_USER";
    }

    // Getters and Setters (ENCAPSULATION)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status=" + status +
                ", userType='" + getUserType() + '\'' +
                '}';
    }
}

