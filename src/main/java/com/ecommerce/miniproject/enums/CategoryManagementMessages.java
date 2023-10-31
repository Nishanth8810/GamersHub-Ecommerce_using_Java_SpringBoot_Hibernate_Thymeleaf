package com.ecommerce.miniproject.enums;

import lombok.Getter;

@Getter
public enum CategoryManagementMessages {

    DUPLICATE_CATEGORY_NAME("Category with same name already exist"),
    ERROR_DELETE("Product is available in this Category , try deleting product first"),
    ERROR_VARIANT_DELETE("This variant is associated with some product")
    ;

    private final String message;
    CategoryManagementMessages(String message) {
        this.message=message;
    }
}
