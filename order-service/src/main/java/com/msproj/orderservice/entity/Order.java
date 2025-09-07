package com.msproj.orderservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List; /**
 * Order Entity - Main order aggregate
 */
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @NotNull(message = "Order date is required")
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "shipping_amount", precision = 10, scale = 2)
    private BigDecimal shippingAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Embedded
    private ShippingAddress shippingAddress;

    @Embedded
    private BillingAddress billingAddress;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_transaction_id")
    private String paymentTransactionId;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    @Column(name = "tracking_number")
    private String trackingNumber;

    // Constructors
    public Order() {
        super();
        this.orderDate = LocalDateTime.now();
    }

    public Order(Long customerId, String customerName, String customerEmail) {
        super();
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.orderDate = LocalDateTime.now();
    }

    // Business Methods
    public void addOrderItem(OrderItem item) {
        item.setOrder(this);
        this.orderItems.add(item);
        recalculateTotals();
    }

    public void removeOrderItem(OrderItem item) {
        item.setOrder(null);
        this.orderItems.remove(item);
        recalculateTotals();
    }

    public void recalculateTotals() {
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountedAmount = totalAmount.subtract(discountAmount);
        BigDecimal amountWithTax = discountedAmount.add(taxAmount);
        this.finalAmount = amountWithTax.add(shippingAmount);
    }

    public void confirm() {
        if (orderItems.isEmpty()) {
            throw new IllegalStateException("Cannot confirm order with no items");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void process() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Can only process confirmed orders");
        }
        this.status = OrderStatus.PROCESSING;
    }

    public void ship(String trackingNumber) {
        if (status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Can only ship processing orders");
        }
        this.status = OrderStatus.SHIPPED;
        this.shippedDate = LocalDateTime.now();
        this.trackingNumber = trackingNumber;
    }

    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Can only deliver shipped orders");
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredDate = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel delivered orders");
        }
        this.status = OrderStatus.CANCELLED;
        this.notes = (notes != null ? notes + " | " : "") + "Cancelled: " + reason;
    }

    // Status check methods
    public boolean isPending() { return OrderStatus.PENDING.equals(status); }
    public boolean isConfirmed() { return OrderStatus.CONFIRMED.equals(status); }
    public boolean isProcessing() { return OrderStatus.PROCESSING.equals(status); }
    public boolean isShipped() { return OrderStatus.SHIPPED.equals(status); }
    public boolean isDelivered() { return OrderStatus.DELIVERED.equals(status); }
    public boolean isCancelled() { return OrderStatus.CANCELLED.equals(status); }

    public int getItemCount() {
        return orderItems.size();
    }

    public int getTotalQuantity() {
        return orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    // Getters and Setters
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

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }

    public BillingAddress getBillingAddress() { return billingAddress; }
    public void setBillingAddress(BillingAddress billingAddress) { this.billingAddress = billingAddress; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }

    public LocalDateTime getShippedDate() { return shippedDate; }
    public void setShippedDate(LocalDateTime shippedDate) { this.shippedDate = shippedDate; }

    public LocalDateTime getDeliveredDate() { return deliveredDate; }
    public void setDeliveredDate(LocalDateTime deliveredDate) { this.deliveredDate = deliveredDate; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + getId() +
                ", customerId=" + customerId +
                ", status=" + status +
                ", finalAmount=" + finalAmount +
                ", itemCount=" + getItemCount() +
                '}';
    }
}
