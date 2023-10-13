package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.OrdersDTO;
import com.ecommerce.miniproject.entity.Orders;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

//@Controller
//public class OrderController {
//
//    @PostMapping("checkout/selectAddress")
//    public String selectAddress(@ModelAttribute("ordersDTO")OrdersDTO ordersDTO){
//        Orders orders=new Orders();
//
//    }
//}
