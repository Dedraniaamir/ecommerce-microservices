package com.msproj.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Product Service Application
 *
 * This service demonstrates:
 * 1. Collections (List, Set, Map) usage
 * 2. Java 8 Stream API features
 * 3. Optional class usage
 * 4. Lambda expressions and method references
 * 5. Functional interfaces
 * 6. Advanced JPA queries with collections
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
		System.out.println("🛍️ Product Service started successfully!");
		System.out.println("🔗 Registered with Service Registry");
		System.out.println("📦 Ready to handle product catalog operations");
	}
}