package com.ecommerce.miniproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String couponCode;

    private int discountAmount;

    private LocalDate expiryDate;

    private int usageLimit;


    public boolean isExpired() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(expiryDate);
    }


}
