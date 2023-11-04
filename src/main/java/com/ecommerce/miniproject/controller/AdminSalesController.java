package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.repository.OrderRepository;
import com.ecommerce.miniproject.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller

public class AdminSalesController {
    @Autowired
    OrderService orderService;

    public AdminSalesController(OrderRepository orderRepository) {
    }

    //    @GetMapping("/admin/sales")
//    public String adminSale(){
//        return "salesReport";
//    }
    @GetMapping("/admin/sales")
    public String chart(Model model) {

        List<LocalDateTime> orderDates = orderService.getOrderDates();
        List<String> dateStrings = orderDates.stream()
                .map(date -> date.toLocalDate().toString())
                .toList();

        int orderCounts = orderService.getOrderCount();
        model.addAttribute("data", dateStrings);
//        model.addAttribute("labels", orderCounts);

        return "salesReport";
    }

}
