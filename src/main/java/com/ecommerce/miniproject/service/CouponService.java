package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.Coupon;
import com.ecommerce.miniproject.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CouponService {

    @Autowired
    CouponRepository couponRepository;

    public List<Coupon> getAllCoupon(){
        return couponRepository.findAll();
    }
    public void saveCoupon(Coupon coupon){
        couponRepository.save(coupon);
    }

    public void deleteCouponById(long id) {
        couponRepository.deleteById(id);
    }
    public Coupon getCouponById(long id) {
        return couponRepository.getReferenceById(id);
    }
    public boolean getCouponByName(String couponCode) {
       return couponRepository.existsByCouponCode(couponCode);
    }
    public Coupon getByCouponCode(String couponCode){
        return couponRepository.findByCouponCode(couponCode);
    }
}
