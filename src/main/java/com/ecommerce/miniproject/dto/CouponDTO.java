package com.ecommerce.miniproject.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class CouponDTO {
    private Long id;

    private String couponCode;

    private int discountAmount;

    private LocalDate expiryDate;

    private int usageLimit;

}
