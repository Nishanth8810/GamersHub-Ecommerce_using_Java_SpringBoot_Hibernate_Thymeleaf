package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.global.GlobalData;
import com.ecommerce.miniproject.service.CategoryService;
import com.ecommerce.miniproject.service.ProductService;
import com.ecommerce.miniproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller

public class HomeController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;
    @GetMapping({"/","home","index"})
    public String home(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isBoolean = userService.isUserBlockedByEmail(authentication.getName());
        if (!isBoolean){
            return "redirect:/logout";
        }

       else{
           return "index";
       }
    }
    @GetMapping("/shop")
    public String shop(Model model){

        model.addAttribute("cartCount", GlobalData.cart.size());

        model.addAttribute("categories",categoryService.getAllCategory());
        model.addAttribute("products",productService.getAllProduct());
        return "shop";
    }
    @GetMapping("/shop/category/{id}")
    public String shopByCategory(@PathVariable int id, Model model){
        model.addAttribute("cartCount",GlobalData.cart.size());

        model.addAttribute("categories",categoryService.getAllCategory());
        model.addAttribute("products",productService.getAllProductsByCategory_id(id));
        return "shop";
    }
    @GetMapping("/shop/viewproduct/{id}")
    public String viewProduct(@PathVariable int id,Model model){
        model.addAttribute("cartCount",GlobalData.cart.size());

        model.addAttribute("product",productService.getProductById(id).get());
        return "viewProduct";
    }

}
