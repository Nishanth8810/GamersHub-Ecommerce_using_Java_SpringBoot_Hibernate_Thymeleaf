package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.dto.UserDTO;
import com.ecommerce.miniproject.entity.Cart;
import com.ecommerce.miniproject.entity.Role;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.entity.Wallet;
import com.ecommerce.miniproject.repository.RoleRepository;
import com.ecommerce.miniproject.repository.UserRepository;
import com.ecommerce.miniproject.util.EmailUtil;
import com.ecommerce.miniproject.util.OtpUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    OtpUtil otpUtil;

    @Autowired
    EmailUtil emailUtil;
    @Autowired
    CartService cartService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    WalletService walletService;

    public List<User> getAllUser() {

        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {

        return userRepository.findById(id);

    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void removeUserById(int id) {
        userRepository.deleteById(id);
    }


    public Optional<User> getUserByEmail(String email) throws NoSuchElementException {
        return userRepository.findUserByEmail(email);

    }

    public boolean isUserBlockedByEmail(String email) {

        Optional<User> optionalUser = getUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.isActive();
        }
        return false;
    }


    ///////otp///////


    public void register(UserDTO userDTO) {
        String otp = otpUtil.generateOtp();

        try {
            emailUtil.sendOtpEmail(userDTO.getEmail(), otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setEmail(userDTO.getEmail());
        user.setLastName(userDTO.getLastName());
        String password = userDTO.getPassword();
        user.setPassword(bCryptPasswordEncoder.encode(password));
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findById(2).get());
        user.setRoles(roles);
        user.setActive(true);
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
         Wallet wallet=new Wallet();
         wallet.setBalance(0.0);
         wallet.setUserId(user.getId());
         walletService.saveWallet(wallet);

    }


    public int verifyOtp(String otp, String email) {
        User user = userRepository.findUserByEmail(email).get();

        if (!user.getOtp().equals(otp)) {
            return 1;
        } else if (Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() > 2 * 60) {
            return 3;
        } else if (user.getOtp().equals(otp) && Duration.between
                (user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < (2 * 60)) {
            user.setOtpActive(true);
            userRepository.save(user);
            Cart cart = new Cart();
            cart.setUser(user);
            cartService.save(cart);
            return 2;
        }
        return 0;
    }

    public void regenerateOtp(String email) throws MessagingException {
        User user = userRepository.findUserByEmail(email).get();
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

    }

    public List<User> findUserByKeyword(String keyword) {
        return userRepository.findByFirstNameContaining(keyword);
    }
    /////////////forgot Password///////////////

    public void sendOTP(String email){
        String otp = otpUtil.generateOtp();

        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        User user = userRepository.findUserByEmail(email).orElseThrow();
        user.setOtpGeneratedTime(LocalDateTime.now());
        user.setOtp(otp);
        userRepository.save(user);
    }

    public int verifyOtpForForgotPassword(String email, String otp) {
        User user = userRepository.findUserByEmail(email).orElseThrow();

        if (!user.getOtp().equals(otp)) {
            return 1;
        } else if (Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() > 2 * 60) {
            return 3;
        } else if (user.getOtp().equals(otp) && Duration.between
                (user.getOtpGeneratedTime(),
                        LocalDateTime.now()).getSeconds() < (2 * 60)) {
            return 2;
        }
        return 0;
    }
}