package com.msproj.orderservice.dto;

import com.msproj.orderservice.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; /**
 * Order Response DTO
 */
public class OrderResponseDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private PaymentMethod paymentMethod;
    private String paymentTransactionId;
    private String trackingNumber;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
    private ShippingAddress shippingAddress;
    private BillingAddress billingAddress;
    private String notes;
    private List<OrderItemResponseDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Factory method to convert from entity
    public static OrderResponseDto fromEntity(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.id = order.getId();
        dto.customerId = order.getCustomerId();
        dto.customerName = order.getCustomerName();
        dto.customerEmail = order.getCustomerEmail();
        dto.orderDate = order.getOrderDate();
        dto.status = order.getStatus();
        dto.totalAmount = order.getTotalAmount();
        dto.taxAmount = order.getTaxAmount();
        dto.shippingAmount = order.getShippingAmount();
        dto.discountAmount = order.getDiscountAmount();
        dto.finalAmount = order.getFinalAmount();
        dto.paymentMethod = order.getPaymentMethod();
        dto.paymentTransactionId = order.getPaymentTransactionId();
        dto.trackingNumber = order.getTrackingNumber();
        dto.shippedDate = order.getShippedDate();
        dto.deliveredDate = order.getDeliveredDate();
        dto.shippingAddress = order.getShippingAddress();
        dto.billingAddress = order.getBillingAddress();
        dto.notes = order.getNotes();
        dto.createdAt = order.getCreatedAt();
        dto.updatedAt = order.getUpdatedAt();

        // Convert order items
        dto.items = order.getOrderItems().stream()
                .map(OrderItemResponseDto::fromEntity)
                .toList();

        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getShippingAmount() { return shippingAmount; }
    public void setShippingAmount(BigDecimal shippingAmount) { this.shippingAmount = shippingAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public LocalDateTime getShippedDate() { return shippedDate; }
    public void setShippedDate(LocalDateTime shippedDate) { this.shippedDate = shippedDate; }

    public LocalDateTime getDeliveredDate() { return deliveredDate; }
    public void setDeliveredDate(LocalDateTime deliveredDate) { this.deliveredDate = deliveredDate; }

    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }

    public BillingAddress getBillingAddress() { return billingAddress; }
    public void setBillingAddress(BillingAddress billingAddress) { this.billingAddress = billingAddress; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<OrderItemResponseDto> getItems() { return items; }
    public void setItems(List<OrderItemResponseDto> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
