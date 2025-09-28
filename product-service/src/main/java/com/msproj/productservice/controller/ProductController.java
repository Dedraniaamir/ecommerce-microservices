package com.msproj.productservice.controller;

import com.google.common.base.Strings;
import com.msproj.productservice.dto.*;
import com.msproj.productservice.entity.Product;
import com.msproj.productservice.entity.ProductStatus;
import com.msproj.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Product Controller demonstrating REST API with Java 8 features
 *
 * REST API patterns and Java 8 integration:
 * 1. RESTful endpoints design
 * 2. Optional usage in request handling
 * 3. Stream operations for data transformation
 * 4. Method references and lambda expressions
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Create a new product
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductRequestDto requestDto) {
        logger.info("POST /api/products - Creating product: {}", requestDto.getName());

        ProductResponseDto response = productService.createProduct(requestDto);

        logger.info("Product created with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get product by ID
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductsByIds(@PathVariable Long id) {
        logger.debug("GET /api/products/{} - Fetching product", id);

        ProductResponseDto response = productService.getProductById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Product>> getProductsByIdsList(@RequestBody List<Long> id) {
        logger.debug("GET /api/products/{} - Fetching product", id);

        List<Product> response = productService.getProductByIdsList(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all products with optional filtering
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "sortBy", required = false, defaultValue = "name") String sortBy) {

        logger.debug("GET /api/products - Fetching products with filters");

        // OPTIONAL usage: Handle optional query parameters
        if (!Strings.isNullOrEmpty(name)  || minPrice != null || maxPrice!= null ||
                categoryId!= null || !Strings.isNullOrEmpty(status)) {

            // Build search criteria using BUILDER pattern
            ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                    .name(Strings.isNullOrEmpty(name) ? name : null)
                    .priceRange(minPrice, maxPrice)
                    .category(categoryId)
                    .statuses(StringUtils.isNotBlank(status) ?
                            List.of(ProductStatus.valueOf(status.toUpperCase())) :
                            null)
                    .sortBy(sortBy)
                    .build();

            List<ProductResponseDto> products = productService.searchProducts(criteria);
            return ResponseEntity.ok(products);
        }

        // No filters - return all products
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by category
     * GET /api/products/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable Long categoryId) {
        logger.debug("GET /api/products/category/{} - Fetching products by category", categoryId);

        List<ProductResponseDto> products = productService.getProductsByCategory(categoryId);

        return ResponseEntity.ok(products);
    }

    /**
     * Advanced filtering endpoint
     * POST /api/products/filter
     */
    @PostMapping("/filter")
    public ResponseEntity<List<ProductResponseDto>> filterProducts(@RequestBody ProductFilterDto filters) {
        logger.debug("POST /api/products/filter - Advanced filtering");

        List<ProductResponseDto> products = productService.getProductsWithFilters(filters);

        return ResponseEntity.ok(products);
    }

    /**
     * Search products by tags
     * GET /api/products/tags
     */
    @GetMapping("/tags")
    public ResponseEntity<List<ProductResponseDto>> getProductsByTags(
            @RequestParam List<String> tags) {
        logger.debug("GET /api/products/tags - Searching by tags: {}", tags);

        // JAVA 8 STREAMS: Process tag list
        List<String> processedTags = tags.stream()
                .map(String::toLowerCase)  // Method reference
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())  // Remove empty tags
                .distinct()  // Remove duplicates
                .collect(Collectors.toList());

        List<ProductResponseDto> products = productService.getProductsByTags(processedTags);

        return ResponseEntity.ok(products);
    }

    /**
     * Get products grouped by category
     * GET /api/products/grouped/category
     */
    @GetMapping("/grouped/category")
    public ResponseEntity<Map<String, List<ProductResponseDto>>> getProductsGroupedByCategory() {
        logger.debug("GET /api/products/grouped/category - Grouping products by category");

        Map<String, List<ProductResponseDto>> groupedProducts = productService.getProductsGroupedByCategory();

        return ResponseEntity.ok(groupedProducts);
    }

    /**
     * Get product statistics
     * GET /api/products/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ProductStatisticsDto> getProductStatistics() {
        logger.debug("GET /api/products/statistics - Calculating statistics");

        ProductStatisticsDto statistics = productService.getProductStatistics();

        return ResponseEntity.ok(statistics);
    }

    /**
     * Get advanced analytics
     * GET /api/products/analytics
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAdvancedAnalytics() {
        logger.debug("GET /api/products/analytics - Advanced analytics");

        Map<String, Object> analytics = productService.getAdvancedProductAnalytics();

        return ResponseEntity.ok(analytics);
    }

    /**
     * Get top rated products
     * GET /api/products/top-rated
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<ProductResponseDto>> getTopRatedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        logger.debug("GET /api/products/top-rated - Fetching top {} rated products", limit);

        List<ProductResponseDto> products = productService.getTopRatedProducts(limit);

        return ResponseEntity.ok(products);
    }

    /**
     * Get low stock products
     * GET /api/products/low-stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDto>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        logger.debug("GET /api/products/low-stock - Fetching products with stock <= {}", threshold);

        List<ProductResponseDto> products = productService.getLowStockProducts(threshold);

        return ResponseEntity.ok(products);
    }

    /**
     * Update product stock
     * PATCH /api/products/{id}/stock
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponseDto> updateStock(
            @PathVariable Long id,
            @RequestBody UpdateStockRequestDto requestDto) {
        logger.info("PATCH /api/products/{}/stock - Operation: {}, Quantity: {}",
                id, requestDto.getOperation(), requestDto.getQuantity());

        // JAVA 8: Use method reference and functional approach
        Integer quantity = "REDUCE".equalsIgnoreCase(requestDto.getOperation())
                ? -Math.abs(requestDto.getQuantity())  // Make negative for reduction
                : Math.abs(requestDto.getQuantity());   // Make positive for addition

        ProductResponseDto response = productService.updateStock(id, quantity);

        return ResponseEntity.ok(response);
    }

    /**
     * Add product review
     * POST /api/products/{id}/reviews
     */
    @PostMapping("/{id}/reviews")
    public ResponseEntity<Void> addProductReview(
            @PathVariable Long id,
            @RequestBody @Valid CreateReviewRequestDto requestDto) {
        logger.info("POST /api/products/{}/reviews - Adding review from customer: {}",
                id, requestDto.getCustomerId());

        requestDto.setProductId(id);  // Ensure product ID matches path
        productService.addProductReview(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Get popular tags
     * GET /api/products/tags/popular
     */
    @GetMapping("/tags/popular")
    public ResponseEntity<List<String>> getPopularTags(
            @RequestParam(defaultValue = "20") int limit) {
        logger.debug("GET /api/products/tags/popular - Fetching top {} tags", limit);

        List<String> popularTags = productService.getPopularTags(limit);

        return ResponseEntity.ok(popularTags);
    }

    /**
     * Get recommended products
     * GET /api/products/{id}/recommendations
     */
    @GetMapping("/{id}/recommendations")
    public ResponseEntity<List<ProductResponseDto>> getRecommendedProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") int limit) {
        logger.debug("GET /api/products/{}/recommendations - Fetching {} recommendations", id, limit);

        List<ProductResponseDto> recommendations = productService.getRecommendedProducts(id, limit);

        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get products with advanced search
     * GET /api/products/search
     * Demonstrates complex query parameter handling with OPTIONAL
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProductsAdvanced(
            @RequestParam Optional<String> query,
            @RequestParam Optional<String> category,
            @RequestParam Optional<String> minPrice,
            @RequestParam Optional<String> maxPrice,
            @RequestParam Optional<List<String>> tags,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<String> sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.debug("GET /api/products/search - Advanced search with query: {}", query.orElse("all"));

        // OPTIONAL CHAINING and STREAMS: Process optional parameters
        ProductFilterDto filters = new ProductFilterDto();
        filters.setName(query.orElse(null));
        filters.setMinPrice(minPrice.map(BigDecimal::new).orElse(null));
        filters.setMaxPrice(maxPrice.map(BigDecimal::new).orElse(null));
        filters.setTags(tags.orElse(Collections.emptyList()));
        filters.setSortBy(sortBy.orElse("name"));
        filters.setSortOrder(sortOrder.orElse("asc"));
        filters.setPage(page);
        filters.setSize(size);

        // Get filtered products
        List<ProductResponseDto> products = productService.getProductsWithFilters(filters);

        // COLLECTIONS: Build response with metadata
        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", products.size());
        response.put("filters", filters);

        return ResponseEntity.ok(response);
    }

    /**
     * Bulk operations endpoint demonstrating STREAMS processing
     * POST /api/products/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> bulkOperations(@RequestBody BulkOperationRequestDto requestDto) {
        logger.info("POST /api/products/bulk - Operation: {}", requestDto.getOperation());

        Map<String, Object> result = new HashMap<>();

        // JAVA 8 STREAMS: Process bulk operations
        switch (requestDto.getOperation().toUpperCase()) {
            case "CREATE_MULTIPLE" -> {
                List<ProductResponseDto> createdProducts = requestDto.getProducts()
                        .stream()
                        .map(productService::createProduct)  // Method reference
                        .collect(Collectors.toList());

                result.put("createdProducts", createdProducts);
                result.put("count", createdProducts.size());
            }

            case "UPDATE_PRICES" -> {
                // Example: Update prices by percentage
                BigDecimal multiplier = requestDto.getPriceMultiplier();
                List<Long> productIds = requestDto.getProductIds();

                List<ProductResponseDto> updatedProducts = productIds.stream()
                        .map(id -> {
                            try {
                                ProductResponseDto product = productService.getProductById(id);
                                // This would need additional service method for price update
                                return product;
                            } catch (Exception e) {
                                logger.warn("Failed to update product {}: {}", id, e.getMessage());
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)  // Remove nulls from failed updates
                        .collect(Collectors.toList());

                result.put("updatedProducts", updatedProducts);
                result.put("count", updatedProducts.size());
            }

            default -> {
                result.put("error", "Unsupported bulk operation: " + requestDto.getOperation());
            }
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Get product insights using advanced Java 8 features
     * GET /api/products/{id}/insights
     */
    @GetMapping("/{id}/insights")
    public ResponseEntity<Map<String, Object>> getProductInsights(@PathVariable Long id) {
        logger.debug("GET /api/products/{}/insights - Calculating insights", id);

        ProductResponseDto product = productService.getProductById(id);
        List<ProductResponseDto> recommendations = productService.getRecommendedProducts(id, 5);

        // JAVA 8 STREAMS: Calculate various insights
        Map<String, Object> insights = new HashMap<>();

        // Basic product info
        insights.put("product", product);
        insights.put("recommendations", recommendations);

        // OPTIONAL: Safe calculations
        Optional<Double> avgRating = Optional.ofNullable(product.getAverageRating())
                .filter(rating -> rating > 0);
        insights.put("hasGoodRating", avgRating.map(rating -> rating >= 4.0).orElse(false));

        // STREAMS: Tag analysis
        Set<String> productTags = Optional.ofNullable(product.getTags()).orElse(Collections.emptySet());
        List<String> popularTags = productService.getPopularTags(10);

        // SET operations: Find which product tags are popular
        Set<String> popularProductTags = productTags.stream()
                .filter(popularTags::contains)  // Method reference with contains
                .collect(Collectors.toSet());
        insights.put("popularTagsUsed", popularProductTags);

        // Price comparison with category average
        Map<String, Object> analytics = productService.getAdvancedProductAnalytics();
        @SuppressWarnings("unchecked")
        Map<String, Double> categoryAvgPrices = (Map<String, Double>) analytics.get("categoryAveragePrices");

        Optional<Double> categoryAvg = Optional.ofNullable(categoryAvgPrices.get(product.getCategoryName()));
        insights.put("priceVsCategoryAverage",
                categoryAvg.map(avg -> product.getPrice().doubleValue() - avg).orElse(0.0));

        return ResponseEntity.ok(insights);
    }

    /**
     * Get product trends using STREAMS and DATE operations
     * GET /api/products/trends
     */
    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getProductTrends() {
        logger.debug("GET /api/products/trends - Analyzing product trends");

        Map<String, Object> trends = new HashMap<>();

        // This could include time-based analysis
        List<ProductResponseDto> allProducts = productService.getAllProducts();

        // STREAMS: Trend analysis
        Map<String, Long> dailyCreations = allProducts.stream()
                .collect(Collectors.groupingBy(
                        product -> product.getCreatedAt().toLocalDate().toString(),
                        Collectors.counting()
                ));
        trends.put("dailyCreations", dailyCreations);

        // Popular categories
        Map<String, Long> categoryPopularity = allProducts.stream()
                .filter(product -> product.getIsAvailable())
                .collect(Collectors.groupingBy(
                        ProductResponseDto::getCategoryName,
                        Collectors.counting()
                ));
        trends.put("categoryPopularity", categoryPopularity);

        return ResponseEntity.ok(trends);
    }
}

