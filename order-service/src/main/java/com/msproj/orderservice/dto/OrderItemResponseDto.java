package com.msproj.orderservice.dto;

import com.msproj.orderservice.entity.OrderItem;

import java.math.BigDecimal; /**
 * Order Item Response DTO
 */
public class OrderItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;

    public static OrderItemResponseDto fromEntity(OrderItem item) {
        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.id = item.getId();
        dto.productId = item.getProductId();
        dto.productName = item.getProductName();
        dto.productSku = item.getProductSku();
        dto.unitPrice = item.getUnitPrice();
        dto.quantity = item.getQuantity();
        dto.subtotal = item.getSubtotal();
        dto.discountAmount = item.getDiscountAmount();
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
}
