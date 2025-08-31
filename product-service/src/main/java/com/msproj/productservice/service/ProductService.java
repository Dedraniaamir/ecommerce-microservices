package com.msproj.productservice.service;

import com.msproj.productservice.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Product Service Interface
 */
public interface ProductService {
    ProductResponseDto createProduct(CreateProductRequestDto requestDto);

    ProductResponseDto getProductById(Long id);

    List<ProductResponseDto> getAllProducts();

    List<ProductResponseDto> getProductsByCategory(Long categoryId);

    List<ProductResponseDto> searchProducts(ProductSearchCriteria criteria);

    List<ProductResponseDto> getProductsByTags(List<String> tags);

    Map<String, List<ProductResponseDto>> getProductsGroupedByCategory();

    ProductStatisticsDto getProductStatistics();

    List<ProductResponseDto> getTopRatedProducts(int limit);

    List<ProductResponseDto> getLowStockProducts(int threshold);

    ProductResponseDto updateStock(Long productId, Integer quantity);

    void addProductReview(CreateReviewRequestDto requestDto);

    List<String> getPopularTags(int limit);

    List<ProductResponseDto> getRecommendedProducts(Long productId, int limit);

    Map<String, Object> getAdvancedProductAnalytics();

    @Transactional(readOnly = true)
    Map<String, Object> getProductAnalytics();

    List<ProductResponseDto> getProductsWithFilters(ProductFilterDto filters);

    @Transactional(readOnly = true)
    List<ProductResponseDto> findSimilarProducts(Long productId);
}
