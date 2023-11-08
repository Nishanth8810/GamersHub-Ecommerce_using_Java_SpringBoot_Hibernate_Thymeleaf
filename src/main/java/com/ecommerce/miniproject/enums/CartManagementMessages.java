package com.ecommerce.miniproject.enums;

import lombok.Getter;

@Getter
public enum CartManagementMessages {
   ERROR_VARIANT("This combination of variant is not available"),
    ALREADY_PRESENT("item is already in your cart :)"),
    ADDED_CART("Added to cart");
    private final String message;

    CartManagementMessages(String message) {
        this.message = message;
    }
}
