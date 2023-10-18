package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.OrdersDTO;
import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.repository.OrderItemRepository;
import com.ecommerce.miniproject.repository.OrderRepository;
import com.ecommerce.miniproject.repository.OrderStatusRepository;
import com.ecommerce.miniproject.repository.PaymentMethodRepository;
import com.ecommerce.miniproject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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


    @PostMapping("/checkout/confirmOrder")
    public String confirmOrder(@ModelAttribute("selectedAddress")int id,
                               @ModelAttribute("paymentMethod")String paymentMethod ,
                                Model model, Principal principal){

      double tot=  cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().stream().map(item->item.getProduct().getPrice()*item.getQuantity()).reduce(0.0, Double::sum);

      List<CartItem>cartItemList= cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems();
     long address= addressService.getAddressOfUser(id).getId();
//        List<Long> quantities = cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get()
//                .getCartItems()
//                .stream()
//                .mapToLong(CartItem::getQuantity)
//                .boxed()
//                .collect(Collectors.toList());

//        System.out.println(quantities);

        User user= userService.getUserByEmail(principal.getName()).get();
        Orders orders=new Orders();
        orders.setAddress(addressService.getAddressById(id));
//        orders.setId();
        orders.setUser(user);
        orders.setPaymentMethod(paymentMethodRepository.findById(1L).get());
        orders.setOrderStatus(orderStatusRepository.findById(1L).get());
        orders.setLocalDateTime(LocalDateTime.now());
//        orders.setOrderItems();
        orders.setAmount((int) tot);
        orderRepository.save(orders);

        Cart cart = cartService.findCartByUser(user).get();
        List<CartItem> cartItemLists = cart.getCartItems();
//
//
//        OrderItem orderItem=new OrderItem();
//        orderItem.setProduct();
        for (CartItem cartItem : cartItemLists) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrders(orders); // Set the order for the order item
            orderItemRepository.save(orderItem);
        }




        return "redirect:/checkout";


    }




//   @PostMapping
}
