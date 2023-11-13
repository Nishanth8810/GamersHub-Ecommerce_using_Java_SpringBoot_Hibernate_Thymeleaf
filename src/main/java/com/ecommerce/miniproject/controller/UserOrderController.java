package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.enums.OrderManagementMessages;
import com.ecommerce.miniproject.enums.UserManagementMessages;
import com.ecommerce.miniproject.repository.*;
import com.ecommerce.miniproject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
public class UserOrderController {
    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderStatusRepository orderStatusRepository;
    @Autowired
    PaymentMethodRepository paymentMethodService;
    @Autowired
    WalletService walletService;
    @Autowired
    ProductService productService;
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    RatingService ratingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;


    @GetMapping("/user/order/cancel/{id}")
    public String getCancelOrder(@PathVariable long id,
                                 RedirectAttributes redirectAttributes) {

        Orders orders = orderService.getOrderById(id).orElseThrow();
        User user = orders.getUser();
        List<OrderItem>orderItems=orders.getOrderItems();
        for (OrderItem orderItem:orderItems){
            Product product=orderItem.getProduct();
            product.setQuantity(product.getQuantity()+orderItem.getQuantity());
            productService.addProduct(product);
        }
        boolean isWallet = orders.getPaymentMethod() == paymentMethodService.findById(3L).orElse(null) ||
                (paymentMethodService.findById(2L).isPresent() &&
                        orders.getPaymentMethod() == paymentMethodService.findById(2L).get());
        if (isWallet) {
            Wallet wallet = walletService.getWalletOfUser(user.getId());
            double newBalance = orders.getAmount() + wallet.getBalance();
            wallet.setBalance(newBalance);
            walletService.saveWallet(wallet);
            redirectAttributes.addFlashAttribute("successRefund", UserManagementMessages.SUCCESS_REFUND.getMessage());
            orders.setOrderStatus(orderStatusRepository.findById(5L).orElseThrow());
            orderService.saveOrder(orders);
            return "redirect:/user/orders";
        }
        orders.setOrderStatus(orderStatusRepository.findById(5L).orElseThrow());
        orderService.saveOrder(orders);
        return "redirect:/user/orders";
    }

    @GetMapping("/user/orders")
    public String getOrders(Model model, Principal principal) {
        String loggedUser = principal.getName();
        User user = userService.getUserByEmail(loggedUser).orElseThrow();
        Collections.reverse(user.getOrders());
        List<Orders>ordersList=orderRepository.findByUserId(user.getId());
        Collections.reverse(ordersList);
        model.addAttribute("orderDetails", ordersList);
        return "userOrders";
    }

    @GetMapping("/user/order/viewOrder/{id}")
    public String getUserOrder(@PathVariable long id, Model model) {
        Orders order = orderService.getOrderById(id).orElseThrow();
        model.addAttribute("orderList", order);
        return "userViewOrder";
    }

    @GetMapping("user/order/return/{id}")
    public String getReturnOrder(@PathVariable long id) {
        Orders order = orderService.getOrderById(id).orElseThrow();
        order.setOrderStatus(orderStatusRepository.findById(7L).orElseThrow());
        orderService.saveOrder(order);
        return "redirect:/user/orders";
    }

    @PostMapping("/addRatingReview")
    public String postReview(@RequestParam("rating")int id,
                             @RequestParam("review")String review,
                             @RequestParam("productId")int productId,Principal principal,
                             RedirectAttributes redirectAttributes) {
        Product product=productService.getProductById(productId).orElseThrow();
        User user=userService.getUserByEmail(principal.getName()).orElseThrow();
        if (ratingService.isReviewExists(productId, user.getId())){
         redirectAttributes.addFlashAttribute("errorReview",
                 "You have already rated this product");
            return "redirect:/user/orders";
        }
        Rating rating=new Rating();
        rating.setRatingValue(id);
        rating.setUser(user);
        rating.setReview(review);
        rating.setProduct(product);
        ratingRepository.save(rating);
        redirectAttributes.addFlashAttribute("success",
                OrderManagementMessages.SUCCESS_REVIEW.getMessage());
        return "redirect:/user/orders";

    }
}
