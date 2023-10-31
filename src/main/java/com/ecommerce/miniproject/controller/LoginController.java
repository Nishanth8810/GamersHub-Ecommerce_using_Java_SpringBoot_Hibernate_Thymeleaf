package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.UserDTO;
import com.ecommerce.miniproject.entity.Role;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.RoleRepository;
import com.ecommerce.miniproject.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    RoleRepository roleRepository;

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
                                      @RequestParam("email") String email, Model model) {

        int verification = userService.verifyOtp(otp, email);


        if (verification == 1) {
            model.addAttribute("email", email);
            model.addAttribute("wrongOtp", "Entered otp is wrong");
            return "otpVerification";

        }
        if (verification == 2) {
            model.addAttribute("email", email);
            model.addAttribute("wrongOtp", "OTP is verified");
            return "login";

        }
        if (verification == 3) {
            model.addAttribute("email", email);
            model.addAttribute("wrongOtp", "OTP Timeout");
            return "otpVerification";

        }


        return "login";
    }

    @PostMapping("/resendOTP")
    public String postResendOTP(@ModelAttribute("email") String email, Model model) throws MessagingException {


        userService.regenerateOtp(email);
        model.addAttribute("email", email);

        return "otpVerification";
    }


    ////////forgotPassword/////////////

    @GetMapping("/forgotPassword")
    public String showForgotPasswordForm() {
        return "forgotPassword";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("email") String email, Model model) {
        User user = userService.getUserByEmail(email).orElseThrow();
        List<Role>roles=new ArrayList<>();
        roles.add(roleRepository.findById(3).get());
        roles.add(roleRepository.findById(2).get());
        user.setRoles(roles);
        userService.saveUser(user);
        userService.sendOTP(email);
        model.addAttribute("email", user.getEmail());
        return "otpVerificationForgot";
    }

    @PostMapping("/forgotVerifyAccount")
    public String postOtpForgotVerification(@RequestParam("otp") String otp,
                                            @RequestParam("email") String email, Model model) {

        System.out.println(otp + " " + email);

        int verification = userService.verifyOtpForForgotPassword(email, otp);


        if (verification == 1) {
            model.addAttribute("email", email);
            model.addAttribute("wrongOtp", "Entered otp is wrong");
            return "otpVerificationForgot";

        }
        if (verification == 2) {
            model.addAttribute("email", email);
            model.addAttribute("wrongOtp", "OTP is verified");
            return "changePasswordForgot";

        }
        if (verification == 3) {
            model.addAttribute("email", email);
            model.addAttribute("wrongOtp", "OTP Timeout");
            return "otpVerificationForgot";

        }
        return "login";
    }


    @PostMapping("/changePassword")
    public String getChangePassword(@RequestParam("newPass") String newPass,
                                    @RequestParam("confirmPass") String confirmPass, Model model,
                                    @RequestParam("email") String email,
                                    RedirectAttributes redirectAttributes) {

        User user = userService.getUserByEmail(email).orElseThrow();
        if (!Objects.equals(newPass, confirmPass)) {
            model.addAttribute("errorConfirmPass", "Passwords must be same");
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(newPass));
            userService.saveUser(user);
             redirectAttributes.addFlashAttribute("passSuccess", "password changed");
            return "redirect:/login";
        }
    return "changePasswordForgot";
    }
}

