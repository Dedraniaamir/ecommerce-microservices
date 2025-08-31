package com.msproj.productservice.dto;

import com.msproj.productservice.entity.Product;
import com.msproj.productservice.entity.ProductStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function; /**
 * Product Search Criteria using Java 8 features
 */
public class ProductSearchCriteria {
    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long categoryId;
    private List<List<ProductStatus>> statuses;
    private List<String> tags;
    private String sortBy = "name";

    // FUNCTIONAL INTERFACE: Custom filter function
    private Optional<Function<Product, Boolean>> customFilter = Optional.empty();

    // BUILDER pattern
    public static class Builder {
        private ProductSearchCriteria criteria = new ProductSearchCriteria();

        public Builder name(String name) {
            criteria.name = name;
            return this;
        }

        public Builder priceRange(BigDecimal min, BigDecimal max) {
            criteria.minPrice = min;
            criteria.maxPrice = max;
            return this;
        }

        public Builder category(Long categoryId) {
            criteria.categoryId = categoryId;
            return this;
        }

        public Builder statuses(List<ProductStatus> statuses) {
            criteria.statuses = Arrays.asList(statuses);
            return this;
        }

        public Builder tags(String... tags) {
            criteria.tags = Arrays.asList(tags);
            return this;
        }

        public Builder sortBy(String sortBy) {
            criteria.sortBy = sortBy;
            return this;
        }

        // FUNCTIONAL INTERFACE: Add custom filter
        public Builder customFilter(Function<Product, Boolean> filter) {
            criteria.customFilter = Optional.of(filter);
            return this;
        }

        public ProductSearchCriteria build() {
            return criteria;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public List<List<ProductStatus>> getStatuses() { return statuses; }
    public void setStatuses(List<ProductStatus> statuses) { this.statuses = Collections.singletonList(statuses); }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public Optional<Function<Product, Boolean>> getCustomFilter() { return customFilter; }
    public void setCustomFilter(Optional<Function<Product, Boolean>> customFilter) { this.customFilter = customFilter; }

    @Override
    public String toString() {
        return "ProductSearchCriteria{" +
                "name='" + name + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", categoryId=" + categoryId +
                ", statuses=" + statuses +
                ", tags=" + tags +
                ", sortBy='" + sortBy + '\'' +
                '}';
    }
}
