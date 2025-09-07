package com.msproj.orderservice.fallback;

import com.msproj.orderservice.client.ProductServiceClient;
import com.msproj.orderservice.dto.ProductDto;
import com.msproj.orderservice.exception.ServiceUnavailableException;
import com.msproj.orderservice.request.ProductAvailabilityRequest;
import com.msproj.orderservice.request.StockUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ProductServiceFallback implements ProductServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceFallback.class);

    @Override
    public ProductDto getProductById(Long id) {
        logger.warn("Product Service unavailable, using fallback for getProductById: {}", id);
        return new ProductDto(id, "Product Unavailable", "Product information temporarily unavailable",
                BigDecimal.ZERO, 0, "UNAVAILABLE", "INACTIVE", "Unknown",
                Set.of(), Map.of(), 0.0, false);
    }

    @Override
    public List<ProductDto> getProductsByIds(List<Long> productIds) {
        logger.warn("Product Service unavailable, using fallback for getProductsByIds: {}", productIds);
        return productIds.stream()
                .map(this::getProductById)
                .toList();
    }

    @Override
    public ProductDto updateProductStock(Long productId, StockUpdateRequest request) {
        logger.error("Product Service unavailable, cannot update stock for product: {}", productId);
        throw new ServiceUnavailableException("Product Service unavailable for stock update");
    }

    @Override
    public Map<Long, Boolean> checkProductsAvailability(List<ProductAvailabilityRequest> requests) {
        logger.warn("Product Service unavailable, returning false for all availability checks");
        return requests.stream()
                .collect(java.util.stream.Collectors.toMap(
                        req -> req.productId(),
                        req -> false // Conservative fallback
                ));
    }
}
