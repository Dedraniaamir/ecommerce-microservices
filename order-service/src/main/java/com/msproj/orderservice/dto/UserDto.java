package com.msproj.orderservice.dto;

import java.time.LocalDateTime; /**
 * DTOs for inter-service communication
 */

// User Service DTOs
public record   UserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String fullName,
        String userType,
        Integer loyaltyPoints,
        String customerTier,
        LocalDateTime createdAt
) {}
