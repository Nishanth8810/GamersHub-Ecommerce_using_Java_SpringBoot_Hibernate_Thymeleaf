package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.repository.OrderRepository;
import com.ecommerce.miniproject.service.ChartService;
import com.ecommerce.miniproject.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller

public class AdminSalesController {
    @Autowired
    OrderService orderService;

    @Autowired
    ChartService chartService;

    public AdminSalesController(OrderRepository orderRepository) {
    }

    //    @GetMapping("/admin/sales")
//    public String adminSale(){
//        return "salesReport";
//    }
    @GetMapping("/admin/sales")
    public String chart(Model model) {

        List<List<Object>> weekly = chartService.weeklyReport();
        Collections.reverse(weekly.get(0));
        Collections.reverse(weekly.get(1));
        model.addAttribute("data", weekly.get(0));
        model.addAttribute("labels", weekly.get(1));
        return "salesReport";
    }

}
