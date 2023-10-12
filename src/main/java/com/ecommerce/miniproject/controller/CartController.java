package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.AddressDTO;
import com.ecommerce.miniproject.entity.Address;
import com.ecommerce.miniproject.entity.Product;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.global.GlobalData;
import com.ecommerce.miniproject.service.AddressService;
import com.ecommerce.miniproject.service.ProductService;
import com.ecommerce.miniproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    ProductService productService;

    @Autowired
    AddressService addressService;
    @Autowired
    UserService userService;



    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable int id){
        GlobalData.cart.add(productService.getProductById(id).get());
        return "redirect:/shop";

    }
    @GetMapping("/cart")
    public String cartGet(Model model){
        model.addAttribute("cartCount",GlobalData.cart.size());
        model.addAttribute("total",GlobalData.cart.stream().mapToDouble(Product::getPrice).sum());
        model.addAttribute("cart",GlobalData.cart);
        return "cart";
    }

    @GetMapping("/cart/removeItem/{index}")
        public String removeCartItem(@PathVariable int index){

        GlobalData.cart.remove(index);
        return "redirect:/cart";

        }
    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal){model.addAttribute("total",
            GlobalData
            .cart.stream()
            .mapToDouble(Product::getPrice).sum());
        model.addAttribute("addressDTO",new AddressDTO());

       String loggedUser= principal.getName();
        List<Address>addressList=addressService.getAddressOfUser(loggedUser);
        model.addAttribute("addressList",addressList);


        return "checkout";
    }


    @PostMapping("/checkout")
    public String postCheckout(@ModelAttribute AddressDTO addressDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userService.getUserByEmail(currentPrincipalName).get();

            Address address = new Address();


            address.setId(addressDTO.getId());
            address.setName(addressDTO.getName());
            address.setCity(addressDTO.getCity());
            address.setPhoneNo(addressDTO.getPhoneNo());
            address.setLandmark(addressDTO.getLandmark());
            address.setUser(user);
            address.setPincode(addressDTO.getPincode());
            address.setAddress(addressDTO.getAddress());
            addressService.addAddress(address);


            return "redirect:/checkout";

        }




    }
