package com.msproj.orderservice.dto;

import com.msproj.orderservice.entity.OrderStatus;

// Additional DTOs for controller operations
public class OrderSearchCriteriaDto {
    private Long customerId;
    private OrderStatus status;
    private String fromDate;
    private String toDate;
    private String minAmount;
    private String maxAmount;
    private String customerEmail;
    private String customerName;

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getFromDate() { return fromDate; }
    public void setFromDate(String fromDate) { this.fromDate = fromDate; }

    public String getToDate() { return toDate; }
    public void setToDate(String toDate) { this.toDate = toDate; }

    public String getMinAmount() { return minAmount; }
    public void setMinAmount(String minAmount) { this.minAmount = minAmount; }

    public String getMaxAmount() { return maxAmount; }
    public void setMaxAmount(String maxAmount) { this.maxAmount = maxAmount; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
