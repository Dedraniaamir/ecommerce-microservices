package com.msproj.productservice.dto;

import com.msproj.productservice.entity.ProductStatus;

import java.math.BigDecimal;
import java.util.List; /**
 * Product Filter DTO for advanced filtering
 */
public class ProductFilterDto {
    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long categoryId;
    private List<ProductStatus> statuses;
    private List<String> tags;
    private String sortBy = "name";
    private String sortOrder = "asc";
    private int page = 0;
    private int size = 10;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public List<ProductStatus> getStatuses() { return statuses; }
    public void setStatuses(List<ProductStatus> statuses) { this.statuses = statuses; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    @Override
    public String toString() {
        return "ProductFilterDto{" +
                "name='" + name + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", categoryId=" + categoryId +
                ", statuses=" + statuses +
                ", tags=" + tags +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
