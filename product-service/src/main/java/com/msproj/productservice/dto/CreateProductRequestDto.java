package com.msproj.productservice.dto;

import com.msproj.productservice.entity.Category;
import com.msproj.productservice.entity.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.*; /**
 * Create Product Request DTO with Collections
 */
public class CreateProductRequestDto {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String sku;

    // COLLECTIONS in DTO
    private Set<String> tags = new HashSet<>();
    private Map<String, String> attributes = new HashMap<>();
    private List<String> imageUrls = new ArrayList<>();

    // Constructors
    public CreateProductRequestDto() {}

    // BUILDER PATTERN with FLUENT API
    public static class Builder {
        private CreateProductRequestDto dto = new CreateProductRequestDto();

        public Builder name(String name) {
            dto.name = name;
            return this;
        }

        public Builder description(String description) {
            dto.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            dto.price = price;
            return this;
        }

        public Builder stockQuantity(Integer stockQuantity) {
            dto.stockQuantity = stockQuantity;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            dto.categoryId = categoryId;
            return this;
        }

        public Builder sku(String sku) {
            dto.sku = sku;
            return this;
        }

        public Builder tags(String... tags) {
            dto.tags.addAll(Arrays.asList(tags));
            return this;
        }

        public Builder attribute(String key, String value) {
            dto.attributes.put(key, value);
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            dto.imageUrls.add(imageUrl);
            return this;
        }

        public CreateProductRequestDto build() {
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Convert DTO to Entity using Collections operations
    public Product toEntity(Category category) {
        Product product = new Product(name, description, price, stockQuantity, category);
        product.setSku(sku);

        // COLLECTIONS: Copy all collections
        product.getTags().addAll(this.tags);
        product.getAttributes().putAll(this.attributes);
        product.getImageUrls().addAll(this.imageUrls);

        return product;
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

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
