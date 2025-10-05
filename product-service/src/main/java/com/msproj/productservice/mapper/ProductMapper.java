package com.msproj.productservice.mapper;

import com.msproj.productservice.dto.ProductDto;
import com.msproj.productservice.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getSku(),
                product.getStatus().getDisplayName(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getTags(),
                product.getAttributes(),
                product.getAverageRating(),
                product.getStockQuantity() > 0
        );
    }
}
