package com.msproj.orderservice.dto;

import com.msproj.orderservice.entity.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Create Order Request DTO
 */
public class CreateOrderRequestDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<CreateOrderItemDto> items;

    @Valid
    private ShippingAddress shippingAddress;

    @Valid
    private BillingAddress billingAddress;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Valid
    private PaymentDetailsDto paymentDetails;

    private String notes;

    // Constructors
    public CreateOrderRequestDto() {}

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public List<CreateOrderItemDto> getItems() { return items; }
    public void setItems(List<CreateOrderItemDto> items) { this.items = items; }

    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }

    public BillingAddress getBillingAddress() { return billingAddress; }
    public void setBillingAddress(BillingAddress billingAddress) { this.billingAddress = billingAddress; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public PaymentDetailsDto getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(PaymentDetailsDto paymentDetails) { this.paymentDetails = paymentDetails; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

