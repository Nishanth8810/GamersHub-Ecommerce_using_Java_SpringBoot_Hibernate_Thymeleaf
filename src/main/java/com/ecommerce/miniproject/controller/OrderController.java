package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.OrdersDTO;
import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.repository.*;
import com.ecommerce.miniproject.service.*;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrderController {
    @Autowired
    AddressService addressService;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    PaymentMethodService paymentMethodService;

    @Autowired
    OrderService orderService;
    @Autowired
    CartService cartService;
    @Autowired
    UserService userService;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/checkout/confirmOrder")
    public String confirmOrder(@ModelAttribute("selectedAddress") int id,
                                Principal principal,Model model) {

        double tot = cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().stream().map(item -> item.getProduct().getPrice() * item.getQuantity()).reduce(0.0, Double::sum);


        User user = userService.getUserByEmail(principal.getName()).get();
        Orders orders = new Orders();
        orders.setAddress(addressService.getAddressById(id));
        orders.setUser(user);
        orders.setPaymentMethod(paymentMethodRepository.findById(1L).get());
        orders.setOrderStatus(orderStatusRepository.findById(1L).get());
        orders.setLocalDateTime(LocalDateTime.now());
        orders.setAmount((int) tot);
        orderService.saveOrder(orders);

        Cart cart = cartService.findCartByUser(user).get();
        List<CartItem> cartItemLists = cart.getCartItems();

        for (CartItem cartItem : cartItemLists) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrders(orders);
            orderItemService.saveOrderItem(orderItem);
        }

        model.addAttribute("selectedAddress",addressService.getAddressById(id));



        return "redirect:/orderSuccess";


    }

    @GetMapping("/orderSuccess")
    public String getOrderSuccess(Model model, Principal principal) {

        User user = userService.getUserByEmail(principal.getName()).get();

        List<Orders> userOrders = user.getOrders();

        userOrders.sort(Comparator.comparing(Orders::getId).reversed());

        if (!userOrders.isEmpty()) {
            Orders lastOrder = userOrders.get(0);
            Long lastOrderId = lastOrder.getId();

          Orders orders=  orderService.getOrderById(lastOrderId).get();

            // Add the last order ID to the model
            model.addAttribute("orderId", lastOrderId);

//            model.addAttribute("address",orders.getAddress());
//            model.addAttribute("orderId");
            model.addAttribute("orderItem",orders.getOrderItems());
            model.addAttribute("total",orders.getAmount());


            return "orderSuccess";

        }

        return "orderSuccess";
//   @PostMapping
    }}
