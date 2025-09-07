package com.msproj.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Security Configuration for API Gateway
 *
 * Security Features Demonstrated:
 * 1. OAuth2 Resource Server with JWT validation
 * 2. Keycloak integration for authentication
 * 3. Role-based access control (RBAC)
 * 4. Stateless security (no sessions)
 * 5. Custom JWT decoder configuration
 * 6. Path-based security rules
 * 7. CORS integration with security
 * 8. Custom authentication converter
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Main Security Filter Chain Configuration
     *
     * Demonstrates comprehensive security setup:
     * - Public endpoints (no auth required)
     * - Protected endpoints (JWT required)
     * - Admin endpoints (specific roles required)
     * - CORS integration
     * - Stateless session management
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        logger.info("Configuring security filter chain for API Gateway");

        return http
                // Disable CSRF for stateless API
                .csrf(csrf -> csrf.disable())

                // Disable form login and HTTP Basic (using JWT only)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Stateless session management (no server-side sessions)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                // Authorization rules
                .authorizeExchange(exchanges -> exchanges
                        // PUBLIC ENDPOINTS - No authentication required
                        .pathMatchers(HttpMethod.GET,
                                "/api/products/**",           // Public product catalog
                                "/actuator/health/**",        // Health checks
                                "/actuator/info",             // Application info
                                "/fallback/**")               // Fallback endpoints
                        .permitAll()

                        // AUTHENTICATION ENDPOINTS
                        .pathMatchers("/auth/**", "/oauth2/**", "/login/**")
                        .permitAll()

                        // ADMIN ENDPOINTS - Require ADMIN role
                        .pathMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // USER MANAGEMENT - Require USER or ADMIN role
                        .pathMatchers(HttpMethod.GET, "/api/users/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/users")
                        .permitAll() // User registration

                        .pathMatchers("/api/users/**")
                        .hasAnyRole("USER", "ADMIN")

                        // ORDER ENDPOINTS - Require authentication
                        .pathMatchers("/api/orders/**")
                        .hasAnyRole("USER", "CUSTOMER", "ADMIN")

                        // PRODUCT WRITE OPERATIONS - Require ADMIN role
                        .pathMatchers(HttpMethod.POST, "/api/products/**")
                        .hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/products/**")
                        .hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasRole("ADMIN")

                        // MONITORING ENDPOINTS - Require ADMIN role
                        .pathMatchers("/actuator/**")
                        .hasRole("ADMIN")

                        // WebSocket endpoints
                        .pathMatchers("/ws/**")
                        .hasAnyRole("USER", "CUSTOMER", "ADMIN")

                        // All other requests require authentication
                        .anyExchange().authenticated()
                )

                // OAuth2 Resource Server configuration
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(reactiveJwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )

                // Exception handling
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )

                .build();
    }

    /**
     * JWT Decoder Configuration for Keycloak
     *
     * Demonstrates:
     * 1. Keycloak JWT validation
     * 2. Custom JWT decoder setup
     * 3. Issuer validation
     * 4. Algorithm specification
     */
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        logger.info("Configuring JWT decoder for Keycloak integration");

        // For development - using mock JWT decoder
        // In production, replace with actual Keycloak URL
        String keycloakIssuerUri = "http://localhost:8180/realms/ecommerce";

        try {
            return NimbusReactiveJwtDecoder
                    .withIssuerLocation(keycloakIssuerUri)
                    .build();
        } catch (Exception e) {
            logger.warn("Keycloak not available, using mock JWT decoder for development");
            return createMockJwtDecoder();
        }
    }

    /**
     * JWT Authentication Converter
     *
     * Converts JWT claims to Spring Security authorities:
     * 1. Extract roles from JWT
     * 2. Convert to GrantedAuthority objects
     * 3. Handle Keycloak role structure
     */
    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        logger.info("Configuring JWT authentication converter");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    /**
     * Mock JWT Decoder for development when Keycloak is not available
     */
    private ReactiveJwtDecoder createMockJwtDecoder() {
        logger.info("Creating mock JWT decoder for development");

        return new MockReactiveJwtDecoder();
    }

    /**
     * WebClient for external HTTP calls (Keycloak, etc.)
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }
}

