package com.msproj.orderservice.exception;

public class InventoryUpdateException extends OrderServiceException {
    public InventoryUpdateException(String message, Throwable cause) {
        super(message, "INVENTORY_UPDATE_FAILED", cause);
    }
}
