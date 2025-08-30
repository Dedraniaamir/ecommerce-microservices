package com.msproj.userservice.dto;

import com.msproj.userservice.entity.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User DTOs demonstrating modern Java features and proper data transfer
 *
 * Java Features Demonstrated:
 * 1. Records (Java 14+) - Immutable data carriers
 * 2. Pattern matching and validation
 * 3. Builder pattern for complex objects
 */

// RECORD - Modern Java immutable data carrier
public record UserSummaryDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName
) {
    // Records automatically generate:
    // - Constructor
    // - Getters (id(), username(), etc.)
    // - equals(), hashCode(), toString()

    // Custom methods can be added
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

