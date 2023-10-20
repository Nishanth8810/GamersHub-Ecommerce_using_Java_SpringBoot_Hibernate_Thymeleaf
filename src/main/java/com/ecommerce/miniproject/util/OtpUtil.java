package com.ecommerce.miniproject.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpUtil {
    public String generateOtp() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.format("%06d", randomNumber);

    }

}
