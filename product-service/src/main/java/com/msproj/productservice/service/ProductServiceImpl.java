package com.msproj.productservice.service;

import com.msproj.productservice.dto.*;
import com.msproj.productservice.entity.Category;
import com.msproj.productservice.entity.Product;
import com.msproj.productservice.entity.ProductReview;
import com.msproj.productservice.entity.ProductStatus;
import com.msproj.productservice.exception.CategoryNotFoundException;
import com.msproj.productservice.exception.ProductNotFoundException;
import com.msproj.productservice.repository.CategoryRepository;
import com.msproj.productservice.repository.ProductRepository;
import com.msproj.productservice.repository.ProductReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors; /**
 * Product Service Implementation demonstrating Java 8 features and Collections
 * <p>
 * Java 8 Features Demonstrated:
 * 1. Stream API - filter, map, collect, reduce, etc.
 * 2. Optional class for null safety
 * 3. Lambda expressions
 * 4. Method references
 * 5. Functional interfaces
 * 6. Collector operations
 * 7. Parallel streams for performance
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReviewRepository reviewRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ProductResponseDto createProduct(CreateProductRequestDto requestDto) {
        logger.info("Creating product: {}", requestDto.getName());

        // Validate category exists
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + requestDto.getCategoryId()));

        Product product = requestDto.toEntity(category);
        Product savedProduct = productRepository.save(product);

        logger.info("Product created with ID: {}", savedProduct.getId());
        return ProductResponseDto.fromEntity(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        logger.debug("Fetching product by ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        return ProductResponseDto.fromEntity(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        logger.debug("Fetching all products");

        // JAVA 8 STREAMS: Convert entities to DTOs using method reference
        return productRepository.findAll()
                .stream()
                .map(ProductResponseDto::fromEntity)  // Method reference
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        logger.debug("Fetching products by category ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        return productRepository.findByCategory(category)
                .stream()
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProducts(ProductSearchCriteria criteria) {
        logger.debug("Searching products with criteria: {}", criteria);

        // JAVA 8 STREAMS: Complex filtering using multiple predicates
        return productRepository.findAll()
                .stream()
                .filter(createProductFilter(criteria))  // Custom predicate
                .sorted(createProductComparator(criteria.getSortBy()))  // Custom comparator
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByTags(List<String> tags) {
        logger.debug("Fetching products by tags: {}", tags);

        // COLLECTIONS: Work with Set intersection
        return productRepository.findByTagsIn(tags)
                .stream()
                .filter(product -> {
                    // Check if product has ALL specified tags using Set operations
                    Set<String> productTags = product.getTags();
                    Set<String> searchTags = new HashSet<>(tags);
                    return productTags.containsAll(searchTags);
                })
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<ProductResponseDto>> getProductsGroupedByCategory() {
        logger.debug("Grouping products by category");

        // JAVA 8 STREAMS: Grouping collector
        return productRepository.findByStatus(ProductStatus.ACTIVE)
                .stream()
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.groupingBy(
                        dto -> dto.getCategoryName(),  // Grouping key
                        Collectors.toList()  // Downstream collector
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStatisticsDto getProductStatistics() {
        logger.debug("Calculating product statistics");

        List<Product> products = productRepository.findAll();

        // JAVA 8 STREAMS: Multiple statistical operations
        ProductStatisticsDto stats = products.stream()
                .collect(ProductStatisticsDto.collector());  // Custom collector

        // Additional calculations using OPTIONAL and STREAMS
        OptionalDouble avgPrice = products.stream()
                .filter(Product::isActive)
                .mapToDouble(p -> p.getPrice().doubleValue())
                .average();

        stats.setAveragePrice(avgPrice.orElse(0.0));

        // COLLECTIONS: Count by status using Map
        Map<ProductStatus, Long> statusCounts = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getStatus,
                        Collectors.counting()
                ));

        stats.setStatusCounts(statusCounts);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getTopRatedProducts(int limit) {
        logger.debug("Fetching top {} rated products", limit);

        // JAVA 8 STREAMS: Complex sorting with Optional handling
        return productRepository.findByStatus(ProductStatus.ACTIVE)
                .stream()
                .filter(product -> !product.getReviews().isEmpty())  // Has reviews
                .sorted((p1, p2) -> {
                    // Compare by average rating (descending)
                    double rating1 = p1.getAverageRating().orElse(0.0);
                    double rating2 = p2.getAverageRating().orElse(0.0);
                    return Double.compare(rating2, rating1);
                })
                .limit(limit)
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getLowStockProducts(int threshold) {
        logger.debug("Fetching low stock products with threshold: {}", threshold);

        return productRepository.findByStockQuantityLessThanEqual(threshold)
                .stream()
                .filter(Product::isActive)  // Method reference
                .sorted(Comparator.comparing(Product::getStockQuantity))  // Sort by stock ascending
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto updateStock(Long productId, Integer quantity) {
        logger.info("Updating stock for product ID: {}, quantity: {}", productId, quantity);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (quantity > 0) {
            product.addStock(quantity);
        } else {
            product.reduceStock(Math.abs(quantity));
        }

        Product updatedProduct = productRepository.save(product);
        return ProductResponseDto.fromEntity(updatedProduct);
    }

    @Override
    public void addProductReview(CreateReviewRequestDto requestDto) {
        logger.info("Adding review for product ID: {}", requestDto.getProductId());

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        ProductReview review = new ProductReview(
                product,
                requestDto.getCustomerId(),
                requestDto.getCustomerName(),
                requestDto.getRating(),
                requestDto.getComment()
        );

        reviewRepository.save(review);
        logger.info("Review added successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getPopularTags(int limit) {
        logger.debug("Fetching top {} popular tags", limit);

        // JAVA 8 STREAMS: Flatten collections and count occurrences
        return productRepository.findByStatus(ProductStatus.ACTIVE)
                .stream()
                .flatMap(product -> product.getTags().stream())  // Flatten all tags
                .collect(Collectors.groupingBy(
                        Function.identity(),  // Group by tag itself
                        Collectors.counting()  // Count occurrences
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())  // Sort by count desc
                .limit(limit)
                .map(Map.Entry::getKey)  // Get just the tag names
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getRecommendedProducts(Long productId, int limit) {
        logger.debug("Getting recommended products for product ID: {}", productId);

        Product baseProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // JAVA 8 STREAMS: Find similar products based on tags and category
        return productRepository.findByCategory(baseProduct.getCategory())
                .stream()
                .filter(product -> !product.getId().equals(productId))  // Exclude the base product
                .filter(Product::isActive)
                .map(product -> {
                    // Calculate similarity score based on common tags
                    Set<String> commonTags = new HashSet<>(baseProduct.getTags());
                    commonTags.retainAll(product.getTags());  // SET intersection
                    return new ProductWithScore(product, commonTags.size());
                })
                .sorted(Comparator.comparing(ProductWithScore::getScore).reversed())  // Sort by similarity
                .limit(limit)
                .map(ProductWithScore::getProduct)
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProductAnalytics() {
        logger.debug("Calculating product analytics");

        List<Product> allProducts = productRepository.findAll();

        // JAVA 8 STREAMS: Complex analytics using collectors
        Map<String, Object> analytics = new HashMap<>();

        // Total counts
        analytics.put("totalProducts", allProducts.size());
        analytics.put("activeProducts", allProducts.stream().filter(Product::isActive).count());

        // Price analytics
        DoubleSummaryStatistics priceStats = allProducts.stream()
                .filter(Product::isActive)
                .mapToDouble(p -> p.getPrice().doubleValue())
                .summaryStatistics();

        analytics.put("priceStatistics", Map.of(
                "min", priceStats.getMin(),
                "max", priceStats.getMax(),
                "average", priceStats.getAverage(),
                "count", priceStats.getCount()
        ));

        // Category distribution
        Map<String, Long> categoryDistribution = allProducts.stream()
                .filter(Product::isActive)
                .collect(Collectors.groupingBy(
                        p -> p.getCategory().getName(),
                        Collectors.counting()
                ));
        analytics.put("categoryDistribution", categoryDistribution);

        // Stock analytics
        analytics.put("lowStockProducts", allProducts.stream()
                .filter(p -> p.isActive() && p.getStockQuantity() <= 10)
                .count());

        analytics.put("outOfStockProducts", allProducts.stream()
                .filter(p -> p.getStockQuantity() == 0)
                .count());

        // Tag analytics
        Map<String, Long> tagFrequency = allProducts.stream()
                .filter(Product::isActive)
                .flatMap(p -> p.getTags().stream())
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
        analytics.put("topTags", tagFrequency);

        return analytics;
    }

    // PRIVATE HELPER METHODS using FUNCTIONAL INTERFACES

    // FUNCTIONAL INTERFACE: Create a product filter predicate
    private Predicate<Product> createProductFilter(ProductSearchCriteria criteria) {
        return product -> {
            // Combine multiple filter conditions using AND logic
            boolean nameMatch = criteria.getName() == null ||
                    product.getName().toLowerCase().contains(criteria.getName().toLowerCase());

            boolean priceMatch = (criteria.getMinPrice() == null ||
                    product.getPrice().compareTo(criteria.getMinPrice()) >= 0) &&
                    (criteria.getMaxPrice() == null ||
                            product.getPrice().compareTo(criteria.getMaxPrice()) <= 0);

            boolean statusMatch = criteria.getStatuses() == null ||
                    criteria.getStatuses().contains(product.getStatus());

            boolean categoryMatch = criteria.getCategoryId() == null ||
                    product.getCategory().getId().equals(criteria.getCategoryId());

            boolean tagMatch = criteria.getTags() == null ||
                    criteria.getTags().isEmpty() ||
                    product.getTags().stream().anyMatch(criteria.getTags()::contains);

            return nameMatch || statusMatch || (categoryMatch && tagMatch);
        };
    }

    // FUNCTIONAL INTERFACE: Create product comparator
    private Comparator<Product> createProductComparator(String sortBy) {
        return switch (sortBy != null ? sortBy.toLowerCase() : "name") {
            case "price_asc" -> Comparator.comparing(Product::getPrice);
            case "price_desc" -> Comparator.comparing(Product::getPrice).reversed();
            case "name_asc" -> Comparator.comparing(Product::getName);
            case "name_desc" -> Comparator.comparing(Product::getName).reversed();
            case "stock_asc" -> Comparator.comparing(Product::getStockQuantity);
            case "stock_desc" -> Comparator.comparing(Product::getStockQuantity).reversed();
            case "created_asc" -> Comparator.comparing(Product::getCreatedAt);
            case "created_desc" -> Comparator.comparing(Product::getCreatedAt).reversed();
            default -> Comparator.comparing(Product::getName);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsWithFilters(ProductFilterDto filters) {
        logger.debug("Filtering products with: {}", filters);

        // JAVA 8 STREAMS: Chain multiple operations
        return productRepository.findAll()
                .stream()
                .filter(product -> applyFilters(product, filters))  // Custom filter
                .sorted(createSortComparator(filters.getSortBy(), filters.getSortOrder()))
                .skip(filters.getPage() * filters.getSize())  // Pagination
                .limit(filters.getSize())
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // LAMBDA EXPRESSIONS and PREDICATES
    private boolean applyFilters(Product product, ProductFilterDto filters) {
        // Using multiple predicates combined with AND logic
        Predicate<Product> namePredicate = p ->
                filters.getName() == null || p.getName().toLowerCase().contains(filters.getName().toLowerCase());

        Predicate<Product> pricePredicate = p ->
                (filters.getMinPrice() == null || p.getPrice().compareTo(filters.getMinPrice()) >= 0) &&
                        (filters.getMaxPrice() == null || p.getPrice().compareTo(filters.getMaxPrice()) <= 0);

        Predicate<Product> categoryPredicate = p ->
                filters.getCategoryId() == null || p.getCategory().getId().equals(filters.getCategoryId());

        Predicate<Product> statusPredicate = p ->
                filters.getStatuses() == null || filters.getStatuses().contains(p.getStatus());

        // Combine all predicates
        Predicate<Product> combinedPredicate = namePredicate
                .and(pricePredicate)
                .and(categoryPredicate)
                .and(statusPredicate);

        return combinedPredicate.test(product);
    }

    private Comparator<Product> createSortComparator(String sortBy, String sortOrder) {
        Comparator<Product> comparator = switch (sortBy != null ? sortBy : "name") {
            case "name" -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
            case "price" -> Comparator.comparing(Product::getPrice);
            case "stock" -> Comparator.comparing(Product::getStockQuantity);
            case "created" -> Comparator.comparing(Product::getCreatedAt);
            default -> Comparator.comparing(Product::getName);
        };

        return "desc".equalsIgnoreCase(sortOrder) ? comparator.reversed() : comparator;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAdvancedProductAnalytics() {
        logger.debug("Calculating advanced product analytics");

        List<Product> products = productRepository.findAll();
        Map<String, Object> analytics = new HashMap<>();

        // PARALLEL STREAMS for performance on large datasets
        Map<String, Double> categoryAveragePrices = products.parallelStream()
                .filter(Product::isActive)
                .collect(Collectors.groupingBy(
                        p -> p.getCategory().getName(),
                        Collectors.averagingDouble(p -> p.getPrice().doubleValue())
                ));
        analytics.put("categoryAveragePrices", categoryAveragePrices);

        // OPTIONAL chaining
        Optional<Product> mostExpensive = products.stream()
                .filter(Product::isActive)
                .max(Comparator.comparing(Product::getPrice));

        analytics.put("mostExpensiveProduct",
                mostExpensive.map(Product::getName).orElse("No products available"));

        // COLLECTORS: Partitioning by stock availability
        Map<Boolean, List<String>> stockPartition = products.stream()
                .filter(Product::isActive)
                .collect(Collectors.partitioningBy(
                        Product::isInStock,
                        Collectors.mapping(Product::getName, Collectors.toList())
                ));
        analytics.put("inStockProducts", stockPartition.get(true));
        analytics.put("outOfStockProducts", stockPartition.get(false));

        // REDUCE operation for total inventory value
        BigDecimal totalInventoryValue = products.stream()
                .filter(Product::isActive)
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Method reference for reduction
        analytics.put("totalInventoryValue", totalInventoryValue);

        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findSimilarProducts(Long productId) {
        logger.debug("Finding similar products for ID: {}", productId);

        Product baseProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // JAVA 8 STREAMS: Calculate similarity and rank products
        return productRepository.findByCategory(baseProduct.getCategory())
                .stream()
                .filter(p -> !p.getId().equals(productId))  // Exclude the base product
                .filter(Product::isActive)
                .map(product -> calculateSimilarity(baseProduct, product))
                .filter(similarity -> similarity.getScore() > 0)  // Only similar products
                .sorted(Comparator.comparing(ProductSimilarity::getScore).reversed())
                .limit(5)
                .map(ProductSimilarity::getProduct)
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getProductByIdsList(List<Long> id) {
        return productRepository.findAllById(id);
    }

    // LAMBDA EXPRESSIONS: Custom similarity calculation
    private ProductSimilarity calculateSimilarity(Product base, Product candidate) {
        int score = 0;

        // Tag similarity using SET operations
        Set<String> baseTags = base.getTags();
        Set<String> candidateTags = candidate.getTags();
        Set<String> commonTags = new HashSet<>(baseTags);
        commonTags.retainAll(candidateTags);  // Intersection
        score += commonTags.size() * 3;  // Weight tags highly

        // Price similarity
        BigDecimal priceDiff = base.getPrice().subtract(candidate.getPrice()).abs();
        BigDecimal priceThreshold = base.getPrice().multiply(BigDecimal.valueOf(0.3)); // 30% threshold
        if (priceDiff.compareTo(priceThreshold) <= 0) {
            score += 2;
        }

        // Attribute similarity using MAP operations
        Set<String> baseAttrKeys = base.getAttributes().keySet();
        Set<String> candidateAttrKeys = candidate.getAttributes().keySet();
        Set<String> commonAttrKeys = new HashSet<>(baseAttrKeys);
        commonAttrKeys.retainAll(candidateAttrKeys);
        score += commonAttrKeys.size();

        return new ProductSimilarity(candidate, score);
    }

    // Helper classes for internal processing
    private static class ProductWithScore {
        private final Product product;
        private final int score;

        public ProductWithScore(Product product, int score) {
            this.product = product;
            this.score = score;
        }

        public Product getProduct() {
            return product;
        }

        public int getScore() {
            return score;
        }
    }

    private static class ProductSimilarity {
        private final Product product;
        private final int score;

        public ProductSimilarity(Product product, int score) {
            this.product = product;
            this.score = score;
        }

        public Product getProduct() {
            return product;
        }

        public int getScore() {
            return score;
        }
    }
}
