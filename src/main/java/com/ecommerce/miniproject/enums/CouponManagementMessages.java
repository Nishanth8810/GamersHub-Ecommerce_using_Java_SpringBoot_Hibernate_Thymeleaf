package com.ecommerce.miniproject.enums;

import lombok.Getter;

@Getter
public enum CouponManagementMessages {
    ERROR_COUPON("coupon with same name already exits"),
    ERROR_DATE("Enter a valid date"),
    COUPON_SUCCESS("Coupon has applied "),
    COUPON_CANNOT_APPLIED("this coupon cannot be applied to this order"),
    COUPON_LIMIT("Coupon has reached its limit"),
    COUPON_NOT_VALID("Coupon is no longer valid"),
    COUPON_VALID("Enter valid a coupon");
    private  final String message;

    CouponManagementMessages(String message) {
        this.message = message;
    }
}
