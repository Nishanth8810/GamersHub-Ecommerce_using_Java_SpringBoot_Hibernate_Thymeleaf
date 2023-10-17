package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.Cart;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.service.CartService;
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

import java.security.Principal;
import java.util.Optional;

@Controller

public class HomeController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    CartService cartService;
    @GetMapping({"/","home","index"})
    public String home(Model model){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        boolean isBoolean = userService.isUserBlockedByEmail(authentication.getName());
//        if (!isBoolean){
//            return "redirect:/logout";
//        }

//       else{
           model.addAttribute("products",productService.getAllProduct());
           return "index";
//       }
    }
    @GetMapping("/shop")
    public String shop(Model model,Principal principal){

        if (principal==null){
            model.addAttribute("categories",categoryService.getAllCategory());
            model.addAttribute("products",productService.getAllProduct());
            return "shop";
        }

        //Create cart for user
        Optional<Cart> cartOptional = cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get());
        if (cartOptional.isEmpty()) {
            Cart cart = new Cart();
            cart.setUser(userService.getUserByEmail(principal.getName()).get());
            cartService.save(cart);
        }
        model.addAttribute("cartCount", cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().size());


        model.addAttribute("total",cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().stream().map(item->item.getProduct().getPrice()*item.getQuantity()).reduce(0.0, (a, b) -> a + b));

        model.addAttribute("categories",categoryService.getAllCategory());
        model.addAttribute("products",productService.getAllProduct());
        return "shop";
    }
    @GetMapping("/shop/category/{id}")
    public String shopByCategory(@PathVariable int id, Model model, Principal principal){
        model.addAttribute("total",cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().stream().map(item->item.getProduct().getPrice()*item.getQuantity()).reduce(0.0, (a, b) -> a + b));

        model.addAttribute("categories",categoryService.getAllCategory());
        model.addAttribute("products",productService.getAllProductsByCategory_id(id));
        return "shop";
    }
    @GetMapping("/shop/viewproduct/{id}")
    public String viewProduct(@PathVariable int id, Model model, Principal principal) {

        if (principal == null) {

            model.addAttribute("product", productService.getProductById(id).get());
            return "viewProduct";

        } else {
            model.addAttribute("cartCount", cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().size());

            model.addAttribute("total", cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().stream().map(item -> item.getProduct().getPrice() * item.getQuantity()).reduce(0.0, (a, b) -> a + b));

            model.addAttribute("product", productService.getProductById(id).get());
            return "viewProduct";
        }
    }
}
