package com.ecommerce.miniproject.util;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.JavaMailSender;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpEmail(String email, String otp) throws MessagingException {
        SimpleMailMessage message =new SimpleMailMessage();

//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        message.setTo(email);
        message.setSubject("Verify OTP");
        message.setText("Click the following link to verify: http://localhost:8080/verify-account?email=" + email + "&otp=" + otp);

        javaMailSender.send(message);
    }
}
