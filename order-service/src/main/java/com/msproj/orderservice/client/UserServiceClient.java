package com.msproj.orderservice.client;

import com.msproj.orderservice.dto.UserDto;
import com.msproj.orderservice.fallback.UserServiceFallback;
import com.msproj.orderservice.request.LoyaltyPointsRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * User Service Client
 */
@FeignClient(
        name = "user-service",
        fallback = UserServiceFallback.class
)
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    @CircuitBreaker(name = "user-service")
    @Retry(name = "user-service")
    UserDto getUserById(@PathVariable("id") Long id);

    @PostMapping("/api/users/{id}/loyalty-points")
    @CircuitBreaker(name = "user-service")
    @Retry(name = "user-service")
    void addLoyaltyPoints(@PathVariable("id") Long customerId, @RequestBody LoyaltyPointsRequest request);
}

