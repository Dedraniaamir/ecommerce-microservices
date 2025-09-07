package com.msproj.orderservice.fallback;

import com.msproj.orderservice.client.UserServiceClient;
import com.msproj.orderservice.dto.UserDto;
import com.msproj.orderservice.request.LoyaltyPointsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime; /**
 * Fallback implementations for circuit breaker pattern
 */
@Component
public class UserServiceFallback implements UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceFallback.class);

    @Override
    public UserDto getUserById(Long id) {
        logger.warn("User Service unavailable, using fallback for getUserById: {}", id);
        return new UserDto(id, "unknown", "unknown@example.com", "Unknown", "User",
                "Unknown User", "CUSTOMER", 0, "BRONZE", LocalDateTime.now());
    }

    @Override
    public void addLoyaltyPoints(Long customerId, LoyaltyPointsRequest request) {
        logger.warn("User Service unavailable, skipping loyalty points addition for customer: {}", customerId);
        // Do nothing - operation will be retried later
    }
}
