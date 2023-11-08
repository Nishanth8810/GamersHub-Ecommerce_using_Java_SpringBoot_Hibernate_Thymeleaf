package com.ecommerce.miniproject.util;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.JavaMailSender;

@Component
public class EmailUtil {
    @Autowired
   final private JavaMailSender javaMailSender;
    public EmailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    public void sendOtpEmail(String email, String otp) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify OTP");
        message.setText(email + "&otp=" + otp);
        javaMailSender.send(message);
    }
}
