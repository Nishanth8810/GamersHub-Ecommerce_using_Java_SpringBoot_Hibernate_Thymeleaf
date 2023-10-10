package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.CustomUserDetail;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.AuthenticationException;
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

        user.orElseThrow(() -> new UsernameNotFoundException("username is invalid"));
//        if (!user.get().isActive()) {
//            throw new UsernameNotFoundException("user not Active");
//        }
        return user.map(CustomUserDetail::new).get();
    }
}
