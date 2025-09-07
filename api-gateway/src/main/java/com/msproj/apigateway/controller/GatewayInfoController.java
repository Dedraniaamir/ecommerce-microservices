package com.msproj.apigateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map; /**
 * Gateway Information Controller
 * Provides information about the gateway and available services
 */
@RestController
@RequestMapping("/gateway")
public class GatewayInfoController {

    private static final Logger logger = LoggerFactory.getLogger(GatewayInfoController.class);

    /**
     * Get gateway information and available routes
     */
    @GetMapping("/info")
    public Mono<ResponseEntity<Map<String, Object>>> getGatewayInfo() {
        Map<String, Object> gatewayInfo = Map.of(
                "name", "E-Commerce API Gateway",
                "version", "1.0.0",
                "description", "Central API Gateway for E-Commerce Microservices",
                "timestamp", Instant.now().toString(),
                "features", Map.of(
                        "authentication", "OAuth2 JWT with Keycloak",
                        "authorization", "Role-based access control",
                        "rate_limiting", "Per-client rate limiting",
                        "circuit_breaker", "Resilience4j integration",
                        "logging", "Comprehensive request/response logging",
                        "cors", "Cross-origin resource sharing",
                        "load_balancing", "Service discovery with Eureka"
                ),
                "services", Map.of(
                        "user-service", Map.of(
                                "path", "/api/users/**",
                                "description", "User management and authentication",
                                "methods", "GET, POST, PUT, PATCH, DELETE",
                                "auth_required", true
                        ),
                        "product-service", Map.of(
                                "path", "/api/products/**",
                                "description", "Product catalog and inventory",
                                "methods", "GET (public), POST/PUT/PATCH/DELETE (admin only)",
                                "auth_required", "Partial"
                        ),
                        "order-service", Map.of(
                                "path", "/api/orders/**",
                                "description", "Order processing and management",
                                "methods", "GET, POST, PATCH, DELETE",
                                "auth_required", true
                        )
                ),
                "endpoints", Map.of(
                        "health", "/gateway/health",
                        "info", "/gateway/info",
                        "routes", "/gateway/routes",
                        "fallbacks", "/fallback/{service-name}"
                )
        );

        return Mono.just(ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(gatewayInfo));
    }

    /**
     * Get configured routes information
     */
    @GetMapping("/routes")
    public Mono<ResponseEntity<Map<String, Object>>> getRoutesInfo() {
        Map<String, Object> routesInfo = Map.of(
                "total_routes", 8,
                "timestamp", Instant.now().toString(),
                "routes", Map.of(
                        "user-service", Map.of(
                                "pattern", "/api/users/**",
                                "destination", "lb://user-service",
                                "filters", "Logging, JWT Auth, Rate Limiting, Circuit Breaker",
                                "rate_limit", "100 requests/minute"
                        ),
                        "product-service-read", Map.of(
                                "pattern", "/api/products/** (GET)",
                                "destination", "lb://product-service",
                                "filters", "Logging, Rate Limiting, Circuit Breaker",
                                "auth_required", false,
                                "rate_limit", "200 requests/minute"
                        ),
                        "product-service-write", Map.of(
                                "pattern", "/api/products/** (POST/PUT/PATCH/DELETE)",
                                "destination", "lb://product-service",
                                "filters", "Logging, JWT Auth, Rate Limiting, Circuit Breaker",
                                "auth_required", true,
                                "rate_limit", "50 requests/minute"
                        ),
                        "order-service", Map.of(
                                "pattern", "/api/orders/**",
                                "destination", "lb://order-service",
                                "filters", "Logging, JWT Auth, Rate Limiting, Circuit Breaker",
                                "rate_limit", "50 requests/minute"
                        ),
                        "admin-routes", Map.of(
                                "pattern", "/api/admin/**",
                                "destination", "Various services",
                                "filters", "Logging, JWT Auth (Admin Role), Rate Limiting",
                                "rate_limit", "20 requests/minute"
                        )
                )
        );

        return Mono.just(ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(routesInfo));
    }

    /**
     * Get security configuration information
     */
    @GetMapping("/security")
    public Mono<ResponseEntity<Map<String, Object>>> getSecurityInfo() {
        Map<String, Object> securityInfo = Map.of(
                "authentication", Map.of(
                        "type", "OAuth2 JWT",
                        "provider", "Keycloak (or Mock for development)",
                        "token_header", "Authorization: Bearer <token>",
                        "token_validation", "Signature verification + expiration check"
                ),
                "authorization", Map.of(
                        "type", "Role-based access control (RBAC)",
                        "roles", Map.of(
                                "USER", "Basic user access",
                                "CUSTOMER", "Customer-specific operations",
                                "ADMIN", "Administrative access"
                        )
                ),
                "public_endpoints", Map.of(
                        "product_catalog", "/api/products (GET requests)",
                        "health_checks", "/actuator/health, /gateway/health",
                        "info_endpoints", "/gateway/info, /gateway/routes",
                        "user_registration", "/api/users (POST)"
                ),
                "protected_endpoints", Map.of(
                        "user_management", "/api/users (except registration)",
                        "order_operations", "/api/orders/**",
                        "product_management", "/api/products (write operations)",
                        "admin_operations", "/api/admin/**"
                ),
                "rate_limiting", Map.of(
                        "enabled", true,
                        "strategy", "Per-client token bucket",
                        "limits", Map.of(
                                "user_service", "100/minute",
                                "product_read", "200/minute",
                                "product_write", "50/minute",
                                "order_service", "50/minute",
                                "admin_operations", "20/minute"
                        )
                )
        );

        return Mono.just(ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(securityInfo));
    }

    /**
     * Test authentication endpoint
     */
    @GetMapping("/test-auth")
    public Mono<ResponseEntity<Map<String, Object>>> testAuth() {
        Map<String, Object> authTest = Map.of(
                "message", "Authentication test endpoint",
                "instructions", Map.of(
                        "step1", "Get a token from your authentication provider",
                        "step2", "Include it in the Authorization header: Bearer <your-token>",
                        "step3", "Access protected endpoints through the gateway",
                        "mock_token", "For development, use: 'Bearer mock-token'"
                ),
                "example_request", "curl -H 'Authorization: Bearer mock-token' http://localhost:8080/api/users",
                "timestamp", Instant.now().toString()
        );

        return Mono.just(ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(authTest));
    }

    /**
     * Gateway health check with detailed status
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> detailedHealth() {
        Map<String, Object> detailedHealth = Map.of(
                "status", "UP",
                "gateway", Map.of(
                        "status", "UP",
                        "version", "1.0.0",
                        "uptime", "Running", // Could calculate actual uptime
                        "memory_usage", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(),
                        "available_memory", Runtime.getRuntime().maxMemory()
                ),
                "circuit_breakers", Map.of(
                        "user-service-cb", "CLOSED",
                        "product-service-read-cb", "CLOSED",
                        "product-service-write-cb", "CLOSED",
                        "order-service-cb", "CLOSED"
                ),
                "filters", Map.of(
                        "logging_filter", "ACTIVE",
                        "jwt_auth_filter", "ACTIVE",
                        "rate_limiting_filter", "ACTIVE",
                        "correlation_id_filter", "ACTIVE"
                ),
                "timestamp", Instant.now().toString(),
                "environment", "development" // Could be injected from properties
        );

        return Mono.just(ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(detailedHealth));
    }
}
