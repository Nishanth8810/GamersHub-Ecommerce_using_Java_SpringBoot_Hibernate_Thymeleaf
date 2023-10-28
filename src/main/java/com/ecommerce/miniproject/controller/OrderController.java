package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.AddressDTO;
import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.repository.*;
import com.ecommerce.miniproject.service.*;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Autowired
    private CartRepository cartRepository;


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
//        model.addAttribute("orderId", orderId);


        return "checkout";
    }


//    @InitBinder
//    public void initBinder(WebDataBinder webDataBinder) {
//        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(false);
//        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
//    }


    @PostMapping("/checkout/confirmOrder")
    public String confirmOrder(@Valid @ModelAttribute("selectedAddress") int id,
                               @ModelAttribute("appliedCoupon") String couponCode,
                               @ModelAttribute("paymentMethod") String paymentMethod,
                               Principal principal,
                               RedirectAttributes redirectAttributes,Model model
    ) throws RazorpayException {

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

        if (coupon!=null){
            int quantity=coupon.getUsageLimit()-1;
            coupon.setUsageLimit(quantity);
            couponService.saveCoupon(coupon);
        }

        if (Objects.equals(paymentMethod, "razorPay")) {

            return handleRazorpayPayment(total, id, couponCode, principal, model,redirectAttributes);
        } else {
            return handleOtherPaymentMethods(total, id,  principal, redirectAttributes, model);
        }



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
//    @GetMapping({"/createTransaction/{amount}"})
//    public String createTransaction(@PathVariable(name = "amount")Double amount) throws RazorpayException {
//        orderService.createTransaction(amount);
//        return "razorPayment";
//
//    }
    private String handleRazorpayPayment(double total, int id, String couponCode,
                                         Principal principal,Model model,
                                         RedirectAttributes redirectAttributes){

        try {
            TransactionDetails transactionDetails = orderService.createTransaction(total);
            String RazorOrderId = transactionDetails.getOrderId();
            // Other Razorpay setup code
            model.addAttribute("orderId", RazorOrderId);
            model.addAttribute("amount", total * 100);
            model.addAttribute("address",id);
            return "razorPayment";
        } catch (RazorpayException e) {
            redirectAttributes.addFlashAttribute("errorCoupon", "Failed to initiate Razorpay payment.");
            return "redirect:/checkout"; // Redirect to the checkout page or an error page
        }
    }

//    @PostMapping("/test")
//    public String test(){
//
//    }
    @PostMapping("/razorOrder")
    public String razorOrder(@ModelAttribute("razorpay_payment_id")String id,
                             Principal principal) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient("rzp_test_6yikyjM4VI0lBk", "U3tFVg9E4NV8nwPuSN5mFji6");
        Payment payment= razorpayClient.payments.fetch(id);
        double amount=payment.get("amount");
        double actualAmount=amount/100.0;
        JSONObject jsonObject=payment.get("notes");
        int addressId=jsonObject.getInt("address");

        User user = userService.getUserByEmail(principal.getName()).get();
        Orders orders = new Orders();
        orders.setAddress(addressService.getAddressById(addressId));
        orders.setUser(user);
        orders.setPaymentMethod(paymentMethodRepository.findById(2L).get());
        orders.setOrderStatus(orderStatusRepository.findById(1L).get());
        orders.setLocalDateTime(LocalDateTime.now());

        orders.setAmount((int) actualAmount);
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

//        redirectAttributes.addFlashAttribute("orderId", orderId);
//        redirectAttributes.addFlashAttribute("selectedAddress", addressService.getAddressById(addressId));
        return "shop";

    }

    private String handleOtherPaymentMethods(double total, int id, Principal principal,
                                             RedirectAttributes redirectAttributes, Model model) {
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

        // Then, you can redirect to the orderSuccess page or a relevant page
    }




}
