package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.CustomUserDetail;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(email);

        user.orElseThrow(() -> new UsernameNotFoundException("username is inavlid"));
        if (!user.get().isActive()) {
            throw new UsernameNotFoundException("user not Active");
        }
        return user.map(CustomUserDetail::new).get();
    }
}

//    public String generateOtp(User user) {
//        try {
//            int randomPIN = (int) (Math.random() * 9000) + 1000;
//            user.setOtp(randomPIN);
//            user.setActive(false);
//            userService.saveUser(user);
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setFrom("nishanths8810@gmail.com");// input the senders email ID
//            msg.setTo(user.getEmail());
//
//            msg.setSubject("Welcome To My Channel");
//            msg.setText("Hello \n\n" +"Your Login OTP :" + randomPIN + ".Please Verify. \n\n"+"Regards \n"+"ABC");
//
//            javaMailSender.send(msg);
//
//            return "success";
//        }catch (Exception e) {
//            e.printStackTrace();
//            return "error";
//        }
    //}

//}
