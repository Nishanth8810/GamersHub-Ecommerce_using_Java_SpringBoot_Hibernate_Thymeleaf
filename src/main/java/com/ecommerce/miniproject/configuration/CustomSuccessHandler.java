//package com.ecommerce.miniproject.configuration;
//
//import com.ecommerce.miniproject.entity.CustomUserDetail;
//import com.ecommerce.miniproject.entity.User;
//import com.ecommerce.miniproject.repository.UserRepository;
//import com.ecommerce.miniproject.service.CustomUserDetailService;
//import com.ecommerce.miniproject.service.UserService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.DefaultRedirectStrategy;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Objects;
//@Component
//public class CustomSuccessHandler implements AuthenticationSuccessHandler {
//
//
//@Autowired
//    CustomUserDetailService customUserDetailService;
//@Autowired
//    UserService userService;
//
//@Autowired
//CustomUserDetail customUserDetail;
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        String redirectUrl = null;
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String username = userDetails.getUsername();
//        User user = userService.getUserByEmail(customUserDetail.getEmail()).get();
//        String output = customUserDetailService.generateOtp(user);
//        if(Objects.equals(output, "success"))
//            redirectUrl="/login/otpVerification";
//
//        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
//    }
//
//}
//
//
//
