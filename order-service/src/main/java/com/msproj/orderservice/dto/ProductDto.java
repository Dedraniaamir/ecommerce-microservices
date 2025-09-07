package com.msproj.orderservice.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

// Product Service DTOs
public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String sku,
        String status,
        String categoryName,
        Set<String> tags,
        Map<String, String> attributes,
        Double averageRating,
        Boolean isAvailable
) {}
