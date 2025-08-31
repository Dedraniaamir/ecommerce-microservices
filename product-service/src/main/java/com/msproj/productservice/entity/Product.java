package com.msproj.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors; /**
 * Product Entity with rich collections usage
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_status", columnList = "status"),
        @Index(name = "idx_product_price", columnList = "price")
})
public class Product extends BaseEntity {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "sku", unique = true, length = 50)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // COLLECTIONS: Product attributes using Map
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_attributes",
            joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value")
    private Map<String, String> attributes = new HashMap<>();

    // COLLECTIONS: Product tags
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    // COLLECTIONS: Product images
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_images",
            joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @OrderColumn(name = "image_order")  // Maintain order of images
    private List<String> imageUrls = new ArrayList<>();

    // COLLECTIONS: Product reviews (One-to-Many)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductReview> reviews = new ArrayList<>();

    // Constructors
    public Product() {
        super();
    }

    public Product(String name, String description, BigDecimal price, Integer stockQuantity, Category category) {
        super();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.status = ProductStatus.ACTIVE;
    }

    // Business methods using JAVA 8 FEATURES and COLLECTIONS

    // OPTIONAL usage for safe operations
    public Optional<String> getAttribute(String attributeName) {
        return Optional.ofNullable(attributes.get(attributeName));
    }

    // MAP operations
    public void addAttribute(String name, String value) {
        this.attributes.put(name.toLowerCase(), value);
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name.toLowerCase());
    }

    // SET operations for tags
    public void addTag(String tag) {
        this.tags.add(tag.toLowerCase().trim());
    }

    public void addTags(String... tags) {
        Arrays.stream(tags)  // STREAM from array
                .map(String::toLowerCase)  // Method reference
                .map(String::trim)
                .forEach(this.tags::add);  // Method reference
    }

    public boolean hasTag(String tag) {
        return this.tags.contains(tag.toLowerCase().trim());
    }

    public void removeTag(String tag) {
        this.tags.remove(tag.toLowerCase().trim());
    }

    // LIST operations for images
    public void addImageUrl(String imageUrl) {
        this.imageUrls.add(imageUrl);
    }

    public void removeImageUrl(String imageUrl) {
        this.imageUrls.remove(imageUrl);
    }

    public Optional<String> getPrimaryImageUrl() {
        return imageUrls.isEmpty() ? Optional.empty() : Optional.of(imageUrls.get(0));
    }

    // JAVA 8 STREAMS: Calculate average rating from reviews
    public OptionalDouble getAverageRating() {
        return reviews.stream()
                .filter(review -> review.getRating() != null)  // Filter out null ratings
                .mapToInt(ProductReview::getRating)
                .average();
    }

    // JAVA 8 STREAMS: Get reviews by rating
    public List<ProductReview> getReviewsByRating(Integer rating) {
        return reviews.stream()
                .filter(review -> rating.equals(review.getRating()))
                .sorted(Comparator.comparing(ProductReview::getCreatedAt).reversed())  // Latest first
                .collect(Collectors.toList());
    }

    // JAVA 8 STREAMS: Get recent reviews
    public List<ProductReview> getRecentReviews(int limit) {
        return reviews.stream()
                .sorted(Comparator.comparing(ProductReview::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // JAVA 8 STREAMS: Count reviews by rating
    public Map<Integer, Long> getReviewCountByRating() {
        return reviews.stream()
                .filter(review -> review.getRating() != null)
                .collect(Collectors.groupingBy(
                        ProductReview::getRating,
                        Collectors.counting()
                ));
    }

    // Business logic methods
    public boolean isActive() {
        return ProductStatus.ACTIVE.equals(this.status);
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public boolean isAvailable() {
        return isActive() && isInStock();
    }

    public void reduceStock(Integer quantity) {
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + stockQuantity + ", Requested: " + quantity);
        }
        this.stockQuantity -= quantity;

        // Auto-update status based on stock
        if (this.stockQuantity == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }
    }

    public void addStock(Integer quantity) {
        this.stockQuantity += quantity;

        // Reactivate if was out of stock
        if (this.status == ProductStatus.OUT_OF_STOCK && this.stockQuantity > 0) {
            this.status = ProductStatus.ACTIVE;
        }
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }

    // FUNCTIONAL INTERFACE example: Custom validator
    @FunctionalInterface
    public interface ProductValidator {
        boolean isValid(Product product);
    }

    // Method that accepts functional interface
    public boolean validateWith(ProductValidator validator) {
        return validator.isValid(this);
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public List<ProductReview> getReviews() { return reviews; }
    public void setReviews(List<ProductReview> reviews) { this.reviews = reviews; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(getId(), product.getId()) ||
                (sku != null && Objects.equals(sku, product.sku));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), sku);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", sku='" + sku + '\'' +
                ", status=" + status +
                ", tagsCount=" + tags.size() +
                ", attributesCount=" + attributes.size() +
                ", imagesCount=" + imageUrls.size() +
                ", reviewsCount=" + reviews.size() +
                '}';
    }
}
