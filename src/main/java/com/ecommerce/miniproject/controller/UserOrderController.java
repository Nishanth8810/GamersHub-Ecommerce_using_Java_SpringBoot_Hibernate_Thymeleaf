package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.OrderStatusRepository;
import com.ecommerce.miniproject.service.AddressService;
import com.ecommerce.miniproject.service.OrderService;
import com.ecommerce.miniproject.service.UserService;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class UserOrderController {
    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;
    @Autowired
    OrderService orderService;

    @Autowired
    OrderStatusRepository orderStatusRepository;


    @GetMapping("/user/order/cancel/{id}")
    public String getCancelOrder(@PathVariable long id){

        Orders orders= orderService.getOrderById(id).get();

        orders.setOrderStatus(orderStatusRepository.findById(5L).get());
        orderService.saveOrder(orders);

        return "redirect:/user/orders";


    }

    @GetMapping("/user/orders")
    public String getOrders(Model model, Principal principal){
        String loggedUser=principal.getName();

        User user=userService.getUserByEmail(loggedUser).get();



        model.addAttribute("orderDetails",  user.getOrders());
        return "userOrders";
    }

    @GetMapping("/user/order/viewOrder/{id}")
    public String getUserOrder(@PathVariable long id,Model model){

        Orders order=orderService.getOrderById(id).get();

        model.addAttribute("orderList",order);

        return "userViewOrder";
    }


}
