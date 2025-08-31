package com.msproj.productservice.repository;

import com.msproj.productservice.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; /**
 * Product Review Repository
 */
@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProductId(Long productId);
    List<ProductReview> findByCustomerId(Long customerId);
    List<ProductReview> findByRating(Integer rating);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.id = :productId")
    Optional<Double> findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT r.rating, COUNT(r) FROM ProductReview r WHERE r.product.id = :productId GROUP BY r.rating")
    List<Object[]> getRatingDistributionByProduct(@Param("productId") Long productId);

    @Query("SELECT r FROM ProductReview r WHERE r.product.id = :productId ORDER BY r.createdAt DESC")
    List<ProductReview> findRecentReviewsByProduct(@Param("productId") Long productId);
}
