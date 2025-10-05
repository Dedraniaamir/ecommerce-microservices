package com.msproj.orderservice.client;

import com.msproj.orderservice.dto.ProductDto;
import com.msproj.orderservice.fallback.ProductServiceFallback;
import com.msproj.orderservice.request.ProductAvailabilityRequest;
import com.msproj.orderservice.request.StockUpdateRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; /**
 * Product Service Client
 */
@FeignClient(
        name = "product-service",
        fallback = ProductServiceFallback.class
)
public interface ProductServiceClient {

    @GetMapping("/api/products/{id}")
    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductByIdFallback")
    @Retry(name = "product-service")
    ProductDto getProductById(@PathVariable("id") Long id);

    @PostMapping("/api/products/batch1")
    @CircuitBreaker(name = "product-service")
    @Retry(name = "product-service")
    List<ProductDto> getProductsByIds(@RequestBody List<Long> productIds);

    @PatchMapping("/api/products/{id}/stock")
    @CircuitBreaker(name = "product-service")
    @Retry(name = "product-service")
    ProductDto updateProductStock(@PathVariable("id") Long productId, @RequestBody StockUpdateRequest request);

    @PostMapping("/api/products/check-availability")
    @CircuitBreaker(name = "product-service")
    Map<Long, Boolean> checkProductsAvailability(@RequestBody List<ProductAvailabilityRequest> requests);
}
