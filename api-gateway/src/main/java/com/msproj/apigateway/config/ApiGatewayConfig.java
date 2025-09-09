package com.msproj.apigateway.config;

/**
 * Required imports
 */

//import com.netflix.eureka.RateLimitingFilter;
import com.msproj.apigateway.config.RateLimitingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.time.Duration;

/**
 * Gateway Configuration for Routing and Filtering
 *
 * Concepts Demonstrated:
 * 1. Programmatic route configuration
 * 2. Service discovery-based routing
 * 3. Custom filter integration
 * 4. Predicate-based routing rules
 * 5. Load balancing configuration
 * 6. Circuit breaker integration
 * 7. CORS configuration for web clients
 */
@Configuration
public class ApiGatewayConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayConfig.class);

    @Autowired
    private LoggingFilter loggingFilter;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private RateLimitingFilter rateLimitingFilter;

    /**
     * Define routes programmatically with advanced filtering
     *
     * Route Features Demonstrated:
     * 1. Path-based routing with wildcards
     * 2. Service discovery integration (lb://)
     * 3. Custom filter chains
     * 4. Circuit breaker configuration
     * 5. Rate limiting per service
     * 6. Request transformation
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        logger.info("Configuring Gateway routes with advanced filtering");

        return builder.routes()
                // USER SERVICE ROUTES
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .filter(loggingFilter.apply(new LoggingFilter.Config("USER-SERVICE")))
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimitingFilter.apply(new RateLimitingFilter.Config(100, Duration.ofMinutes(1))))
                                .circuitBreaker(c -> c
                                        .setName("user-service-cb")
                                        .setFallbackUri("forward:/fallback/user-service"))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, false))
                        )
                        .uri("lb://user-service"))

                // PRODUCT SERVICE ROUTES
                .route("product-service-read", r -> r
                        .path("/api/products/**")
                        .and()
                        .method("GET")
                        .filters(f -> f
                                .filter(loggingFilter.apply(new LoggingFilter.Config("PRODUCT-SERVICE-READ")))
                                .filter(rateLimitingFilter.apply(new RateLimitingFilter.Config(200, Duration.ofMinutes(1))))
                                // Read operations don't require authentication for public catalog
                                .circuitBreaker(c -> c
                                        .setName("product-service-read-cb")
                                        .setFallbackUri("forward:/fallback/product-service"))
                        )
                        .uri("lb://product-service"))

                .route("product-service-write", r -> r
                        .path("/api/products/**")
                        .and()
                        .method("POST", "PUT", "PATCH", "DELETE")
                        .filters(f -> f
                                .filter(loggingFilter.apply(new LoggingFilter.Config("PRODUCT-SERVICE-WRITE")))
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimitingFilter.apply(new RateLimitingFilter.Config(50, Duration.ofMinutes(1))))
                                .circuitBreaker(c -> c
                                        .setName("product-service-write-cb")
                                        .setFallbackUri("forward:/fallback/product-service"))
                        )
                        .uri("lb://product-service"))

                // ORDER SERVICE ROUTES
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .filter(loggingFilter.apply(new LoggingFilter.Config("ORDER-SERVICE")))
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimitingFilter.apply(new RateLimitingFilter.Config(50, Duration.ofMinutes(1))))
                                // Add custom header for order service
                                .addRequestHeader("X-Gateway-Source", "api-gateway")
                                .addRequestHeader("X-Request-Time", String.valueOf(System.currentTimeMillis()))
                                .circuitBreaker(c -> c
                                        .setName("order-service-cb")
                                        .setFallbackUri("forward:/fallback/order-service"))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(2)
                                        .setBackoff(Duration.ofMillis(200), Duration.ofMillis(2000), 2, false))
                        )
                        .uri("lb://order-service"))

                // ADMIN ROUTES (Higher security)
                .route("admin-users", r -> r
                        .path("/api/admin/users/**")
                        .filters(f -> f
                                .filter(loggingFilter.apply(new LoggingFilter.Config("ADMIN-USERS")))
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config("ADMIN")))
                                .filter(rateLimitingFilter.apply(new RateLimitingFilter.Config(20, Duration.ofMinutes(1))))
                                .rewritePath("/api/admin/users/(?<segment>.*)", "/api/users/${segment}")
                        )
                        .uri("lb://user-service"))

                // MONITORING ROUTES (Service health checks)
                .route("service-health", r -> r
                        .path("/health/**")
                        .filters(f -> f
                                .filter(loggingFilter.apply(new LoggingFilter.Config("HEALTH-CHECK")))
                                .rewritePath("/health/(?<service>.*)", "/actuator/health")
                        )
                        .uri("http://localhost:8080"))

                // WEBSOCKET ROUTES (Future enhancement)
                .route("websocket-notifications", r -> r
                        .path("/ws/**")
                        .filters(f -> f
                                .filter(loggingFilter.apply(new LoggingFilter.Config("WEBSOCKET")))
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                        )
                        .uri("lb://notification-service"))

                // CATCH-ALL ROUTE for undefined paths
                .route("not-found", r -> r
                        .path("/**")
                        .filters(f -> f
                                .filter(loggingFilter.apply(new LoggingFilter.Config("NOT-FOUND")))
                                .setStatus(404)
                                .setResponseHeader("Content-Type", "application/json")
                        )
                        .uri("forward:/fallback/not-found"))

                .build();
    }

    /**
     * CORS Configuration for web clients
     * Allows cross-origin requests from web applications
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        logger.info("Configuring CORS for web client access");

        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allow specific origins (configure based on your frontend URLs)
        corsConfig.addAllowedOriginPattern("http://localhost:*");
        corsConfig.addAllowedOriginPattern("https://*.yourdomain.com");

        // Allow all HTTP methods
        corsConfig.addAllowedMethod("*");

        // Allow all headers
        corsConfig.addAllowedHeader("*");

        // Allow credentials (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);

        // Cache preflight requests for 1 hour
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    /**
     * Custom load balancer configuration
     * Demonstrates service instance selection strategies
     */
    @Bean
    public CustomLoadBalancerConfiguration loadBalancerConfig() {
        return new CustomLoadBalancerConfiguration();
    }

    /**
     * Global filter for request correlation ID
     * Adds correlation ID to all requests for distributed tracing
     */
    @Bean
    public GlobalCorrelationIdFilter correlationIdFilter() {
        return new GlobalCorrelationIdFilter();
    }
}


