package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.enums.OrderManagementMessages;
import com.ecommerce.miniproject.enums.UserManagementMessages;
import com.ecommerce.miniproject.repository.OrderStatusRepository;
import com.ecommerce.miniproject.repository.PaymentMethodRepository;
import com.ecommerce.miniproject.repository.RatingRepository;
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
    private RatingRepository ratingRepository;


    @GetMapping("/user/order/cancel/{id}")
    public String getCancelOrder(@PathVariable long id,
                                 RedirectAttributes redirectAttributes) {

        Orders orders = orderService.getOrderById(id).orElseThrow();
        User user = orders.getUser();
        boolean isWallet = orders.getPaymentMethod() == paymentMethodService.findById(3L).orElse(null) ||
                (paymentMethodService.findById(2L).isPresent() && orders.getPaymentMethod() == paymentMethodService.findById(2L).get());
        if (isWallet) {
            Wallet wallet = walletService.getWalletOfUser(user.getId());
            double newBalance = orders.getAmount() + wallet.getBalance();
            wallet.setBalance(newBalance);
            walletService.saveWallet(wallet);
            redirectAttributes.addFlashAttribute("successRefund", UserManagementMessages.SUCCESS_REFUND.getMessage());
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
        model.addAttribute("orderDetails", user.getOrders());
        return "userOrders";
    }

    @GetMapping("/user/order/viewOrder/{id}")
    public String getUserOrder(@PathVariable long id, Model model) {
        Orders order = orderService.getOrderById(id).get();
        model.addAttribute("orderList", order);
        return "userViewOrder";
    }

    @GetMapping("user/order/return/{id}")
    public String getReturnOrder(@PathVariable long id) {
        Orders order = orderService.getOrderById(id).get();
        order.setOrderStatus(orderStatusRepository.findById(7L).get());
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
