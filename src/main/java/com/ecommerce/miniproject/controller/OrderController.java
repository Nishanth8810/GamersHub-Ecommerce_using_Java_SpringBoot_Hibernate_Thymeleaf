package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.OrdersDTO;
import com.ecommerce.miniproject.entity.Address;
import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderController {
    @Autowired
    AddressService addressService;

    @PostMapping("/checkout/confirmOrder")
    public String confirmOrder(@ModelAttribute("selectedAddress")int id,
                               @ModelAttribute("paymentMethod")String paymentMethod ,Model model){
        System.out.println(addressService.getAddressById(id).getAddress());
        System.out.println(paymentMethod);



        return "redirect:/checkout";


    }




//   @PostMapping
}
