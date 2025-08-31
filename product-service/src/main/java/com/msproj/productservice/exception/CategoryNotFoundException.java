package com.msproj.productservice.exception;

public class CategoryNotFoundException extends ProductServiceException {
    public CategoryNotFoundException(String message) {
        super(message, "CATEGORY_NOT_FOUND");
    }

    public CategoryNotFoundException(Long categoryId) {
        super("Category not found with ID: " + categoryId, "CATEGORY_NOT_FOUND");
    }
}
