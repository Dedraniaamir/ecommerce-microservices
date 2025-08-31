package com.msproj.productservice.dto;

import com.msproj.productservice.entity.Product;
import com.msproj.productservice.entity.ProductStatus;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collector; /**
 * Product Statistics DTO with CUSTOM COLLECTOR demonstration
 */
public class ProductStatisticsDto {
    private long totalProducts;
    private long activeProducts;
    private double averagePrice;
    private BigDecimal totalInventoryValue;
    private Map<ProductStatus, Long> statusCounts;
    private Map<String, Long> categoryDistribution;

    // CUSTOM COLLECTOR - Advanced Java 8 feature
    public static Collector<Product, ?, ProductStatisticsDto> collector() {
        return Collector.of(
                ProductStatisticsDto::new,  // Supplier
                (stats, product) -> {       // Accumulator
                    stats.totalProducts++;
                    if (product.isActive()) {
                        stats.activeProducts++;
                    }
                },
                (stats1, stats2) -> {       // Combiner for parallel streams
                    stats1.totalProducts += stats2.totalProducts;
                    stats1.activeProducts += stats2.activeProducts;
                    return stats1;
                }
        );
    }

    // Constructors
    public ProductStatisticsDto() {}

    // Getters and Setters
    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

    public long getActiveProducts() { return activeProducts; }
    public void setActiveProducts(long activeProducts) { this.activeProducts = activeProducts; }

    public double getAveragePrice() { return averagePrice; }
    public void setAveragePrice(double averagePrice) { this.averagePrice = averagePrice; }

    public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }

    public Map<ProductStatus, Long> getStatusCounts() { return statusCounts; }
    public void setStatusCounts(Map<ProductStatus, Long> statusCounts) { this.statusCounts = statusCounts; }

    public Map<String, Long> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Long> categoryDistribution) { this.categoryDistribution = categoryDistribution; }
}
