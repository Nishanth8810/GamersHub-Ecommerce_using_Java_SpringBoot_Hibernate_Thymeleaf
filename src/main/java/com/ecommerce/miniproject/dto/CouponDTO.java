package com.ecommerce.miniproject.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class CouponDTO {
    private Long id;

    private String couponCode;

    private int discountAmount;

    private LocalDate expiryDate;

    private int usageLimit;

}
