package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.AddressDTO;
import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.repository.*;
import com.ecommerce.miniproject.service.*;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    CouponService couponService;


    Map<String, Boolean> userBooleanMap = new HashMap<>();

    Map<String, Double> userDoubleMap = new HashMap<>();


    @GetMapping("/checkout")
    public String checkout(@ModelAttribute("appliedCoupon") String couponCode,
                           @ModelAttribute("totalDiscount") String totalDiscount,
                           Model model, Principal principal) {

        int number = cartService.findCartByUser(userService.getUserByEmail
                (principal.getName()).get()).get().getCartItems().size();
        if (number == 0) {
            return "redirect:/cart";
        }
        if (totalDiscount.isEmpty()) {
            model.addAttribute("total", cartService.findCartByUser
                            (userService.getUserByEmail(principal.getName()).get()).get().getCartItems()
                    .stream()
                    .map(item -> item.getProduct().getPrice() * item.getQuantity()).reduce(0.0, (a, b) -> a + b));
        } else {
            model.addAttribute("total", Double.valueOf(totalDiscount));
        }

        model.addAttribute("addressDTO", new AddressDTO());
        model.addAttribute("couponApplied",couponCode);


        String loggedUser = principal.getName();
        List<Address> addressList = addressService.getAddressOfUser(loggedUser);
        model.addAttribute("addressList", addressList);

        return "checkout";
    }


    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(false);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }


    @PostMapping("/checkout/confirmOrder")
    public String confirmOrder(@Valid @ModelAttribute("selectedAddress") int id,
                               @ModelAttribute("appliedCoupon") String couponCode,
                               Principal principal,
                               RedirectAttributes redirectAttributes
    ) {


        double total;
        if (userBooleanMap.get(userService.getUserByEmail(principal.getName()).get().getEmail())) {
            total = userDoubleMap.get(userService.getUserByEmail(principal.getName()).get().getEmail());
        } else {
            total = cartService.findCartByUser(userService.getUserByEmail
                            (principal.getName()).get()).get().getCartItems()
                    .stream().map(item -> item.getProduct().getPrice() * item.getQuantity())
                    .reduce(0.0, Double::sum);
        }

        userBooleanMap.put(userService.getUserByEmail(principal.getName()).get().getEmail(), false);
        Coupon coupon=couponService.getByCouponCode(couponCode);
        int quantity=coupon.getUsageLimit()-1;
        coupon.setUsageLimit(quantity);
        couponService.saveCoupon(coupon);

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

    @GetMapping("/orderSuccess")
    public String getOrderSuccess(Model model, @ModelAttribute("orderId") long orderId) {

        Orders orders = orderService.getOrderById(orderId).get();
        model.addAttribute("orderItem", orders.getOrderItems());
        model.addAttribute("total", orders.getAmount());
        return "orderSuccess";

    }

    @PostMapping("order/couponCode")
    public String getCoupon(@RequestParam("couponCode") String couponCode, Principal principal, RedirectAttributes redirectAttributes
    ) {

        Coupon coupon = couponService.getByCouponCode(couponCode);
        if (coupon==null){
            redirectAttributes.addFlashAttribute("errorCoupon","Enter valid a coupon");
            return "redirect:/checkout";
        }

        if(coupon.getExpiryDate().isBefore(ChronoLocalDate.from(LocalDateTime.now()))){

            redirectAttributes.addFlashAttribute("errorCoupon","Coupon is no longer valid");
            return "redirect:/checkout";
        }

        if (coupon.getUsageLimit()<=0){
            redirectAttributes.addFlashAttribute("errorCoupon","Coupon has reached its limit");
            return "redirect:/checkout";
        }

        double totalDiscount = cartService.findCartByUser(userService.getUserByEmail
                        (principal.getName()).get()).get().getCartItems()
                .stream().map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(0.0, Double::sum);

        if (totalDiscount<coupon.getDiscountAmount()){
            redirectAttributes.addFlashAttribute("errorCoupon","this coupon cannot be applied to this order");
            return "redirect:/checkout";
        }

        int discount = coupon.getDiscountAmount();
        totalDiscount -= discount;
        userBooleanMap.put(userService.getUserByEmail(principal.getName()).get().getEmail(), true);
        userDoubleMap.put(userService.getUserByEmail(principal.getName()).get().getEmail(), totalDiscount);

        redirectAttributes.addFlashAttribute("successCoupon","Coupon has applied ");
        redirectAttributes.addFlashAttribute("totalDiscount", String.valueOf(totalDiscount));
        redirectAttributes.addFlashAttribute("appliedCoupon",coupon.getCouponCode());

        return "redirect:/checkout";

    }

    ///////////razorPay///////////

    @PreAuthorize("permitAll")
    @GetMapping({"/createTransaction/{amount}"})
    public void createTransaction(@PathVariable(name = "amount")Double amount) throws RazorpayException {
       orderService.createTransaction(amount);

}


}
