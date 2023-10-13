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

    @PostMapping("checkout/selectAddress")
    public String selectAddress(@RequestParam("id") int id , Model model){

        System.out.println(id);

        Address address = addressService.getAddressById(id);

        Orders orders=new Orders();

        orders.setAddress(address);


        return "redirect:/checkout";

    }
}
