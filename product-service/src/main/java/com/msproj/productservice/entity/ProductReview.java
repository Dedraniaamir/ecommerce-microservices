package com.msproj.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min; /**
 * Product Review Entity demonstrating relationships
 */
@Entity
@Table(name = "product_reviews")
public class ProductReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;  // Reference to User Service

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "title")
    private String title;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "verified_purchase")
    private Boolean verifiedPurchase = false;

    // Constructors
    public ProductReview() {
        super();
    }

    public ProductReview(Product product, Long customerId, String customerName, Integer rating, String comment) {
        super();
        this.product = product;
        this.customerId = customerId;
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
    }

    // Business methods
    public boolean isPositiveReview() {
        return rating >= 4;
    }

    public boolean isNegativeReview() {
        return rating <= 2;
    }

    // Getters and Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Boolean getVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(Boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }

    @Override
    public String toString() {
        return "ProductReview{" +
                "id=" + getId() +
                ", productId=" + (product != null ? product.getId() : null) +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", verifiedPurchase=" + verifiedPurchase +
                '}';
    }
}
