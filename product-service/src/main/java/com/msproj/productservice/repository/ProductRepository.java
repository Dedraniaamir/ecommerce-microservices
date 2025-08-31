package com.msproj.productservice.repository;

import com.msproj.productservice.dto.ProductSummaryDto;
import com.msproj.productservice.entity.*;
import com.msproj.productservice.entity.Category;
import com.msproj.productservice.entity.Product;
import com.msproj.productservice.entity.ProductReview;
import com.msproj.productservice.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Product Repository demonstrating advanced JPA queries with Collections
 *
 * Advanced JPA Concepts:
 * 1. Queries involving collections (@ElementCollection)
 * 2. JOIN operations on relationships
 * 3. Aggregate functions with GROUP BY
 * 4. Subqueries and EXISTS clauses
 * 5. Pagination support
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. BASIC QUERIES
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByCategory(Category category);
    Optional<Product> findBySku(String sku);
    List<Product> findByNameContainingIgnoreCase(String name);

    // 2. PRICE RANGE QUERIES
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByPriceGreaterThanEqual(BigDecimal price);
    List<Product> findByPriceLessThanEqual(BigDecimal price);

    // 3. STOCK QUERIES
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
    List<Product> findByStockQuantityLessThanEqual(Integer quantity);

    // 4. COLLECTION QUERIES - Working with @ElementCollection
    @Query("SELECT p FROM Product p JOIN p.tags t WHERE t IN :tags")
    List<Product> findByTagsIn(@Param("tags") List<String> tags);

    @Query("SELECT p FROM Product p JOIN p.attributes a WHERE KEY(a) = :attributeName AND VALUE(a) = :attributeValue")
    List<Product> findByAttribute(@Param("attributeName") String attributeName, @Param("attributeValue") String attributeValue);

    @Query("SELECT p FROM Product p WHERE SIZE(p.tags) >= :minTags")
    List<Product> findProductsWithMinimumTags(@Param("minTags") int minTags);

    // 5. CATEGORY HIERARCHY QUERIES
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId OR p.category.parent.id = :categoryId")
    List<Product> findByCategoryOrParentCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    // 6. COMPLEX FILTERING with multiple conditions
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "p.status IN :statuses " +
            "ORDER BY p.createdAt DESC")
    List<Product> findProductsWithFilters(
            @Param("name") String name,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("categoryId") Long categoryId,
            @Param("statuses") List<ProductStatus> statuses
    );

    // 7. AGGREGATE QUERIES
    @Query("SELECT p.category.name, COUNT(p), AVG(p.price), MIN(p.price), MAX(p.price) " +
            "FROM Product p WHERE p.status = 'ACTIVE' GROUP BY p.category.name")
    List<Object[]> getCategoryStatistics();

    @Query("SELECT p.status, COUNT(p) FROM Product p GROUP BY p.status")
    List<Object[]> getProductCountByStatus();

    // 8. SUBQUERIES and EXISTS
    @Query("SELECT p FROM Product p WHERE EXISTS " +
            "(SELECT r FROM ProductReview r WHERE r.product = p AND r.rating >= 4)")
    List<Product> findProductsWithGoodReviews();

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND " +
            "NOT EXISTS (SELECT r FROM ProductReview r WHERE r.product = p)")
    List<Product> findProductsWithoutReviews();

    // 9. TOP/BOTTOM QUERIES
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.price DESC")
    List<Product> findMostExpensiveProducts();

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.stockQuantity ASC")
    List<Product> findLowStockProducts();

    // 10. DATE-BASED QUERIES
    @Query("SELECT p FROM Product p WHERE p.createdAt >= :date")
    List<Product> findRecentProducts(@Param("date") LocalDateTime date);

    @Query("SELECT p FROM Product p WHERE p.updatedAt >= :date")
    List<Product> findRecentlyUpdatedProducts(@Param("date") LocalDateTime date);

    // 11. MODIFYING QUERIES for bulk operations
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.status = :newStatus WHERE p.status = :currentStatus")
    int bulkUpdateProductStatus(@Param("currentStatus") ProductStatus currentStatus,
                                @Param("newStatus") ProductStatus newStatus);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id IN :productIds")
    int bulkAddStock(@Param("productIds") List<Long> productIds, @Param("quantity") Integer quantity);

    // 12. NATIVE QUERIES for complex operations
    @Query(value = "SELECT p.* FROM products p " +
            "JOIN product_tags pt ON p.id = pt.product_id " +
            "WHERE pt.tag IN :tags " +
            "GROUP BY p.id " +
            "HAVING COUNT(DISTINCT pt.tag) = :tagCount",
            nativeQuery = true)
    List<Product> findProductsWithAllTags(@Param("tags") List<String> tags, @Param("tagCount") int tagCount);

    // 13. PROJECTION QUERIES for performance
    @Query("SELECT new com.msproj.productservice.dto.ProductSummaryDto(p.id, p.name, p.price, p.stockQuantity, p.status) " +
            "FROM Product p WHERE p.status = 'ACTIVE'")
    List<ProductSummaryDto> findActiveProductsSummary();

    @Query("SELECT p.id, p.name, p.price FROM Product p WHERE p.category.id = :categoryId")
    List<Object[]> findProductBasicInfoByCategory(@Param("categoryId") Long categoryId);
}

