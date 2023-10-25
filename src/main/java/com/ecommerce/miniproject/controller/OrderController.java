package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.repository.*;
import com.ecommerce.miniproject.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    AddressService addressService;

    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;
    @Autowired
    CartService cartService;
    @Autowired
    UserService userService;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Autowired CouponService couponService;

    boolean couponApplies=false;



    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(false);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }


    @PostMapping("/checkout/confirmOrder")
    public String confirmOrder(@Valid @ModelAttribute("selectedAddress") int id,
                               Principal principal,
                               RedirectAttributes redirectAttributes
                                    ) {


//        Coupon coupon= couponService.getByCouponCode(couponCode);
//        int discount = coupon.getDiscountAmount();


        double total= cartService.findCartByUser(userService.getUserByEmail
                        (principal.getName()).get()).get().getCartItems()
                .stream().map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(0.0, Double::sum);
//        total -= discount;

//
//        double tot = cartService.findCartByUser(userService.getUserByEmail
//                        (principal.getName()).get()).get().getCartItems()
//                .stream().map(item -> item.getProduct().getPrice() * item.getQuantity())
//                .reduce(0.0, Double::sum);
//        if (couponApplies){
//            total=Total;
//            System.out.println(total);
//        }



        User user = userService.getUserByEmail(principal.getName()).get();
        Orders orders = new Orders();
        orders.setAddress(addressService.getAddressById(id));
        orders.setUser(user);
        orders.setPaymentMethod(paymentMethodRepository.findById(1L).get());
        orders.setOrderStatus(orderStatusRepository.findById(1L).get());
        orders.setLocalDateTime(LocalDateTime.now());
        orders.setAmount((int) total);
        orderService.saveOrder(orders);
        long orderId = orders.getId();

        Cart cart = cartService.findCartByUser(user).get();
        List<CartItem> cartItemLists = cart.getCartItems();

        for (CartItem cartItem : cartItemLists) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrders(orders);
            orderItemService.saveOrderItem(orderItem);
        }
        redirectAttributes.addFlashAttribute("orderId", orderId);
        redirectAttributes.addFlashAttribute("selectedAddress", addressService.getAddressById(id));
        return "redirect:/orderSuccess";
    }

    @PostMapping("order/couponCode")
    public String getCoupon(@RequestParam("couponCode") String couponCode,Principal principal,RedirectAttributes redirectAttributes
    ){

      Coupon coupon= couponService.getByCouponCode(couponCode);

     double totalDiscount= cartService.findCartByUser(userService.getUserByEmail
                        (principal.getName()).get()).get().getCartItems()
                .stream().map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(0.0, Double::sum);
        int discount = coupon.getDiscountAmount();
        totalDiscount -= discount;
        redirectAttributes.addFlashAttribute("Total",totalDiscount);
       this.couponApplies=true;
//        System.out.println(totalDiscount);


      return "redirect:/checkout";

    }





    @GetMapping("/orderSuccess")
    public String getOrderSuccess(Model model, @ModelAttribute("orderId") long orderId) {

        Orders orders = orderService.getOrderById(orderId).get();
        model.addAttribute("orderItem", orders.getOrderItems());
        model.addAttribute("total", orders.getAmount());
        return "orderSuccess";

    }
}
