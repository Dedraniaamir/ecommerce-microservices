package com.msproj.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * User Service Application
 *
 * Learning Points Covered:
 * 1. @EnableDiscoveryClient - Registers this service with Eureka
 * 2. Microservice architecture pattern
 * 3. Service registration and discovery
 *
 * This service handles:
 * - User registration and authentication data
 * - User profile management
 * - User roles and permissions
 * - Customer and Admin user types (Inheritance)
 */
@SpringBootApplication
@EnableDiscoveryClient  // Enables service registration with Eureka
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
		System.out.println("👤 User Service started successfully!");
		System.out.println("🔗 Registered with Service Registry");
		System.out.println("📝 Ready to handle user management operations");
	}
}