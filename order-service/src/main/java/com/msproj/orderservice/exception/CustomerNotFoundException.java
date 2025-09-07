package com.msproj.orderservice.exception;

public class CustomerNotFoundException extends OrderServiceException {
    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with ID: " + customerId, "CUSTOMER_NOT_FOUND");
    }
}
