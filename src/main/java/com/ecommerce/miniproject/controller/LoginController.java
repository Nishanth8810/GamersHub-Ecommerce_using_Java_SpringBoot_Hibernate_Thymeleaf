package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.UserDTO;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping("login")
    public String getLogin() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";

    }

    @GetMapping("register")
    public String getRegister(Model model) {

        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(false);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @PostMapping("/register")
    public String postRegister(@Valid @ModelAttribute("user") UserDTO userDTO,
                               BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        Optional<User> optionalUser = userService.getUserByEmail(userDTO.getEmail());
        if (optionalUser.isPresent()) {
            model.addAttribute("user", userDTO);
            model.addAttribute("errorRegister", "Email already exists");
            return "register";
        }

        userService.register(userDTO);

        model.addAttribute("email", userDTO.getEmail());

        return "otpVerification";
    }


    @PostMapping("/verifyAccount")
    public String postOtpVerification(@RequestParam("otp") String otp,
                                      @RequestParam("email") String email) {

        userService.verifyOtp(otp, email);
        return "login";
    }

    @PostMapping("/resendOTP")
    public String postResendOTP(@ModelAttribute("email") String email, Model model) throws MessagingException {


        userService.regenerateOtp(email);
        model.addAttribute("email", email);

        return "otpVerification";
    }


}
