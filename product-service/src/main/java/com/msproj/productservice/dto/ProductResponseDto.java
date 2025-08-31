package com.msproj.productservice.dto;

import com.msproj.productservice.entity.Product;
import com.msproj.productservice.entity.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*; /**
 * Product Response DTO with computed fields
 */
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String sku;
    private ProductStatus status;
    private String categoryName;
    private Long categoryId;
    private Set<String> tags;
    private Map<String, String> attributes;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields using OPTIONAL and STREAMS
    private Double averageRating;
    private Integer reviewCount;
    private Boolean isAvailable;
    private String primaryImageUrl;

    // FACTORY METHOD with STREAMS and OPTIONAL usage
    public static ProductResponseDto fromEntity(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.id = product.getId();
        dto.name = product.getName();
        dto.description = product.getDescription();
        dto.price = product.getPrice();
        dto.stockQuantity = product.getStockQuantity();
        dto.sku = product.getSku();
        dto.status = product.getStatus();
        dto.categoryName = product.getCategory().getName();
        dto.categoryId = product.getCategory().getId();
        dto.createdAt = product.getCreatedAt();
        dto.updatedAt = product.getUpdatedAt();

        // COLLECTIONS: Create defensive copies
        dto.tags = new HashSet<>(product.getTags());
        dto.attributes = new HashMap<>(product.getAttributes());
        dto.imageUrls = new ArrayList<>(product.getImageUrls());

        // OPTIONAL and STREAMS: Computed fields
        dto.averageRating = product.getAverageRating().orElse(0.0);
        dto.reviewCount = product.getReviews().size();
        dto.isAvailable = product.isAvailable();
        dto.primaryImageUrl = product.getPrimaryImageUrl().orElse(null);

        return dto;
    }

    // STATIC FACTORY METHODS for different use cases
    public static ProductResponseDto fromEntitySummary(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.id = product.getId();
        dto.name = product.getName();
        dto.price = product.getPrice();
        dto.stockQuantity = product.getStockQuantity();
        dto.status = product.getStatus();
        dto.isAvailable = product.isAvailable();
        dto.primaryImageUrl = product.getPrimaryImageUrl().orElse(null);
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public String getPrimaryImageUrl() { return primaryImageUrl; }
    public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
}
