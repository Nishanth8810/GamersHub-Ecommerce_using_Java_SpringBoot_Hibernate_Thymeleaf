package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon,Long> {

    boolean existsByCouponCode(String couponCode);

    Coupon findByCouponCode(String couponCode);
}
