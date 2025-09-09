package com.msproj.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Order Service Application
 *
 * Features:
 * - @Transactional operations
 * - @Async multithreading
 * - OpenFeign inter-service communication
 * - Circuit breaker patterns
 * - Service discovery
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
@EnableTransactionManagement
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("🛒 Order Service started successfully!");
        System.out.println("🔗 Registered with Service Registry");
        System.out.println("📡 OpenFeign clients enabled");
        System.out.println("⚡ Async processing enabled");
        System.out.println("💳 Ready to handle order operations");
    }
}