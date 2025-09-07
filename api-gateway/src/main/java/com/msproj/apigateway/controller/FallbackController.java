package com.msproj.apigateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Fallback Controller for Circuit Breaker responses
 *
 * Provides graceful degradation when services are unavailable:
 * 1. Service-specific fallback responses
 * 2. Proper HTTP status codes
 * 3. Structured error responses
 * 4. User-friendly error messages
 * 5. Circuit breaker state information
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);

    /**
     * User Service Fallback
     * Returns when User Service is unavailable
     */
    @GetMapping("/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        logger.warn("User Service fallback triggered - service appears to be down");

        Map<String, Object> fallbackResponse = Map.of(
                "error", "service_unavailable",
                "service", "user-service",
                "message", "User service is temporarily unavailable. Please try again later.",
                "status", 503,
                "timestamp", Instant.now().toString(),
                "fallback", true,
                "suggestion", "You can continue browsing products or try logging in later"
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Content-Type", "application/json")
                .header("X-Fallback-Reason", "circuit-breaker-open")
                .body(fallbackResponse));
    }

    /**
     * Product Service Fallback
     * Returns cached/minimal product information when Product Service is down
     */
    @GetMapping("/product-service")
    public Mono<ResponseEntity<Map<String, Object>>> productServiceFallback() {
        logger.warn("Product Service fallback triggered - service appears to be down");

        Map<String, Object> fallbackResponse = Map.of(
                "error", "service_unavailable",
                "service", "product-service",
                "message", "Product catalog is temporarily unavailable. Please try again later.",
                "status", 503,
                "timestamp", Instant.now().toString(),
                "fallback", true,
                "suggestion", "You can still view your account information or previous orders",
                "cached_data", Map.of(
                        "available", false,
                        "last_updated", "Service temporarily down",
                        "products", "[]"
                )
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Content-Type", "application/json")
                .header("X-Fallback-Reason", "circuit-breaker-open")
                .body(fallbackResponse));
    }

    /**
     * Order Service Fallback
     * Critical service - provides informative error with next steps
     */
    @GetMapping("/order-service")
    public Mono<ResponseEntity<Map<String, Object>>> orderServiceFallback() {
        logger.error("Order Service fallback triggered - critical service down");

        Map<String, Object> fallbackResponse = Map.of(
                "error", "service_unavailable",
                "service", "order-service",
                "message", "Order processing is temporarily unavailable. Your cart is saved.",
                "status", 503,
                "timestamp", Instant.now().toString(),
                "fallback", true,
                "priority", "high",
                "action_required", Map.of(
                        "message", "Please try placing your order again in a few minutes",
                        "support", "If the issue persists, contact customer support",
                        "cart_status", "Your items remain in your cart"
                )
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Content-Type", "application/json")
                .header("X-Fallback-Reason", "circuit-breaker-open")
                .header("X-Priority", "high")
                .body(fallbackResponse));
    }

    /**
     * Generic service fallback
     */
    @GetMapping("/{serviceName}")
    public Mono<ResponseEntity<Map<String, Object>>> genericServiceFallback(@PathVariable String serviceName) {
        logger.warn("Generic fallback triggered for service: {}", serviceName);

        Map<String, Object> fallbackResponse = Map.of(
                "error", "service_unavailable",
                "service", serviceName,
                "message", String.format("%s is temporarily unavailable", serviceName),
                "status", 503,
                "timestamp", Instant.now().toString(),
                "fallback", true
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Content-Type", "application/json")
                .header("X-Fallback-Reason", "circuit-breaker-open")
                .body(fallbackResponse));
    }

    /**
     * Not Found Fallback
     * For routes that don't match any service
     */
    @GetMapping("/not-found")
    public Mono<ResponseEntity<Map<String, Object>>> notFoundFallback() {
        logger.debug("Not found fallback triggered");

        Map<String, Object> notFoundResponse = Map.of(
                "error", "not_found",
                "message", "The requested resource was not found",
                "status", 404,
                "timestamp", Instant.now().toString(),
                "available_services", Map.of(
                        "users", "/api/users",
                        "products", "/api/products",
                        "orders", "/api/orders"
                )
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("Content-Type", "application/json")
                .body(notFoundResponse));
    }

    /**
     * Health check endpoint for the gateway itself
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> gatewayHealth() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "api-gateway",
                "timestamp", Instant.now().toString(),
                "version", "1.0.0",
                "description", "API Gateway is running normally"
        );

        return Mono.just(ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(health));
    }
}

