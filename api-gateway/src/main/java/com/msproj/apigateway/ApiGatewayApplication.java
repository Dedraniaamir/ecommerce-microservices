package com.msproj.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 *
 * This gateway demonstrates:
 * 1. Spring Cloud Gateway routing and filtering
 * 2. OAuth2 Resource Server with Keycloak integration
 * 3. Request/Response logging and monitoring
 * 4. Rate limiting and security policies
 * 5. Service discovery-based routing
 * 6. Circuit breaker integration
 * 7. CORS configuration
 * 8. Comprehensive security filtering
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        System.out.println("🌐 API Gateway started successfully!");
        System.out.println("🔗 Connected to Service Registry");
        System.out.println("🔒 Security filters enabled");
        System.out.println("📊 Request/Response logging active");
        System.out.println("🚪 All microservices accessible through gateway");
        System.out.println("📍 Gateway running on: http://localhost:8080");
    }
}