package com.ecommerce.miniproject.enums;

import lombok.Getter;

@Getter
public enum OrderManagementMessages {
    WALLET_ERROR("Failed to initiate Razorpay payment."),
    WALLET_INSUFFICIENT("Insufficient amount in wallet"),
    SUCCESS_REVIEW("Your response has been submitted");
    private final String message;


    OrderManagementMessages(String message) {
        this.message = message;
    }
}
