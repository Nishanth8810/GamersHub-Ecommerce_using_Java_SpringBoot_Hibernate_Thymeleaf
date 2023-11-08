package com.ecommerce.miniproject.enums;

import lombok.Getter;

@Getter
public enum UserManagementMessages {


    ERROR_DELETE("Cannot delete this user because this user is associated with some order,try to block user"),
    ERROR_SEARCH("User is not present"),
    ERROR_OTP("Entered otp is wrong"),
    OTP_VERIFIED("OTP is verified"),
    OTP_TIMEOUT("OTP Timeout"),
    PASSWORD_NOT_SAME("Passwords must be same"),
    PASSWORD_SUCCESS("password changed"),
    SUCCESS_REFUND("Your amount has been successfully refunded , kindly check your wallet!"),
    DEFAULT_ADDRESS("Default address changed successfully"),
    ADDRESS_ORDER("This address is associated with an order and cannot be deleted."),;

    private final String message;


    UserManagementMessages(String message) {
        this.message = message;
    }
}
