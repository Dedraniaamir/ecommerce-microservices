package com.msproj.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Service Registry Application using Netflix Eureka
 *
 * Key Learning Points:
 * 1. @EnableEurekaServer - Enables this application as Eureka Server
 * 2. Service Discovery Pattern - Central registry for all microservices
 * 3. Spring Cloud Netflix integration
 *
 * This service will:
 * - Act as a service registry where all microservices register themselves
 * - Provide service discovery capabilities
 * - Enable load balancing and failover
 * - Offer a web dashboard to view registered services
 */
@SpringBootApplication
@EnableEurekaServer  // This annotation makes this app a Eureka Server
public class ServiceRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
        System.out.println("🚀 Service Registry started successfully!");
        System.out.println("📊 Eureka Dashboard: http://localhost:8761");
        System.out.println("🔍 All microservices will register here for service discovery");
    }
}