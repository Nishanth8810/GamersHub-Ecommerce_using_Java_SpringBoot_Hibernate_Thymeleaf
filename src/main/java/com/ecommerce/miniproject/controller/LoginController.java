package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.Role;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.RoleRepository;
import com.ecommerce.miniproject.repository.UserRepository;
import com.ecommerce.miniproject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
     @Autowired
    UserRepository userRepository;
     @Autowired
    RoleRepository roleRepository;
     @Autowired
    UserService userService;

    @GetMapping("login")
    public String getLogin(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if (authentication==null || authentication instanceof AnonymousAuthenticationToken){
            return "login";
        }
        return "redirect:/";
    }
    @GetMapping({"register","/admin/userManagement/addUser"})
    public String getRegister(Model model){
        model.addAttribute("user",new User());

        return "register";
    }
    @GetMapping("/logout")
    public String logout(){
        return "logout";
    }

    @PostMapping({"register","/admin/userManagement/addUser"})
    public String postRegister(@Valid @ModelAttribute ("user") User user ,BindingResult bindingResult, Model model )  {

        if (bindingResult.hasErrors()){
            return "register";
        }

        Optional<User> optionalUser= userService.getUserByEmail(user.getEmail());
        if (optionalUser.isPresent()){
            model.addAttribute("user", user);
            model.addAttribute("errorRegister", "Email already exists");
            return "register";

        }


        String password= user.getPassword();
        user.setPassword(bCryptPasswordEncoder.encode(password));
        List<Role> roles =new ArrayList<>();
        roles.add(roleRepository.findById(2).get());
        user.setRoles(roles);
        user.setActive(true);

        userRepository.save(user);
//        request.login(user.getEmail(),user.getPassword());
        return "redirect:/";
    }
}
