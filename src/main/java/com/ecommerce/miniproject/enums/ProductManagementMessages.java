package com.ecommerce.miniproject.enums;

import lombok.Getter;

@Getter
public enum ProductManagementMessages {
   ERROR_PRODUCT_DELETE("Cannot delete the product because there are existing orders associated with it."),
    PRODUCT_DELETE_SUCCESSFUL("Product Deleted Successfully"),
    PRODUCT_NAME_ERROR("Product with same name already exits"),
    PRODUCT_IMAGE_ERROR("add at least one image"),
    PRODUCT_ERROR("An error occurred while updating the product."),
    PRODUCT_UPDATE_ERROR("An error occurred while updating the product."),
    PRODUCT_NOT_FOUND("no results found");
    private  final String message;

    ProductManagementMessages(String message) {
        this.message = message;
    }
}
