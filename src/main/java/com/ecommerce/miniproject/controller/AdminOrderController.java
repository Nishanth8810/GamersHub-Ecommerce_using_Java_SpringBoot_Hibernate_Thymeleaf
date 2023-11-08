package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.enums.UserManagementMessages;
import com.ecommerce.miniproject.repository.OrderStatusRepository;
import com.ecommerce.miniproject.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminOrderController {



    @Autowired
    OrderService orderService;

    @Autowired
    OrderStatusRepository orderStatusRepository;


    @GetMapping("/admin/orders")
    public String getAdminOrders(Model model) {

        model.addAttribute("userOrder", orderService.getAllOrders());
        return "adminOrders";
    }


    /////////////orderStatusManagement///////////////////


    @GetMapping("/admin/order/cancel/{id}")
    public String getCancelOrder(@PathVariable long id) {

        Orders orders = orderService.getOrderById(id).get();

        orders.setOrderStatus(orderStatusRepository.findById(5L).get());

        orderService.saveOrder(orders);


        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/shipped/{id}")
    public String getShippedOrder(@PathVariable long id) {

        Orders orders = orderService.getOrderById(id).get();

        orders.setOrderStatus(orderStatusRepository.findById(2L).get());

        orderService.saveOrder(orders);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/pending/{id}")
    public String getPendingOrder(@PathVariable long id) {

        Orders orders = orderService.getOrderById(id).get();
        orders.setOrderStatus(orderStatusRepository.findById(6L).get());
        orderService.saveOrder(orders);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/transit/{id}")
    public String getTransitOrder(@PathVariable long id) {

        Orders orders = orderService.getOrderById(id).get();
        orders.setOrderStatus(orderStatusRepository.findById(3L).get());
        orderService.saveOrder(orders);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/delivered/{id}")
    public String getDeliveredOrder(@PathVariable long id) {

        Orders orders = orderService.getOrderById(id).get();
        orders.setOrderStatus(orderStatusRepository.findById(4L).get());
        orderService.saveOrder(orders);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/confirmed/{id}")
    public String getConfirmedOrder(@PathVariable long id) {

        Orders orders = orderService.getOrderById(id).get();
        orders.setOrderStatus(orderStatusRepository.findById(1L).get());
        orderService.saveOrder(orders);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/viewOrderDetails/{id}")
    public String getViewOrderDetails(Model model, @PathVariable long id) {
        Orders orders = orderService.getOrderById(id).get();
        model.addAttribute("orderList", orders);
        return "adminViewOrder";
    }


    @GetMapping("/search/order")
    public String getOrderById(@RequestParam("keyword")int id,Model model){


            try {
                Orders orders=orderService.getOrderById(id).get();
                model.addAttribute("userOrder", orders);
                return "adminOrders";
            }
            catch (Exception e){
                model.addAttribute("errorSearch", UserManagementMessages.ERROR_SEARCH.getMessage());
                model.addAttribute("userOrder", orderService.getAllOrders());
                return "adminOrders";

            }
            /////////DASHBOARD ORDERS////////



    }

}
