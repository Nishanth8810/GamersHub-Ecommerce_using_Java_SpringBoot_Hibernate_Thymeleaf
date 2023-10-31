package com.ecommerce.miniproject.enums;

import lombok.Getter;

@Getter
public enum OrderManagementMessages {
    WALLET_ERROR("Failed to initiate Razorpay payment."),
    WALLET_INSUFFICIENT("Insufficient amount in wallet");
    private final String message;


    OrderManagementMessages(String message) {
        this.message = message;
    }
}
