package com.msproj.orderservice.request;

public record ProductAvailabilityRequest(Long productId, Integer requestedQuantity) {}
