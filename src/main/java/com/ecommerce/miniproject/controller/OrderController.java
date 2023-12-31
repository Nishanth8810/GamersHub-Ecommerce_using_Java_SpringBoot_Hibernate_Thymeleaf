package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.AddressDTO;
import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.enums.CouponManagementMessages;
import com.ecommerce.miniproject.enums.OrderManagementMessages;
import com.ecommerce.miniproject.repository.*;
import com.ecommerce.miniproject.service.*;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;

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
    @Autowired
    WalletService walletService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    Map<String, Boolean> userBooleanMap = new HashMap<>();
    Map<String, Double> userDoubleMap = new HashMap<>();

    @GetMapping("/checkout")
    public String checkout(@ModelAttribute("appliedCoupon") String couponCode,
                           @ModelAttribute("totalDiscount") String totalDiscount,
                           Model model,
                           Principal principal) {

        int number = Objects.requireNonNull(cartService.findCartByUser(userService.getUserByEmail
                (principal.getName()).orElse(null)).orElse(null)).getCartItems().size();
        if (number == 0) {
            return "redirect:/cart";
        }
        if (totalDiscount.isEmpty()) {
            model.addAttribute("total", Objects.requireNonNull(cartService.findCartByUser
                            (userService.getUserByEmail(principal.getName())
                                    .orElse(null)).orElse(null)).getCartItems()
                    .stream()
                    .map(item -> item.getProduct().getPrice() * item.getQuantity()).reduce(0.0, Double::sum));
        } else {
            model.addAttribute("total", Double.valueOf(totalDiscount));
        }

        User user = userService.getUserByEmail(principal.getName()).orElseThrow();
        Wallet wallet = walletService.getWalletOfUser(user.getId());
        model.addAttribute("addressDTO", new AddressDTO());
        model.addAttribute("couponApplied", couponCode);
        model.addAttribute("walletAmount", wallet.getBalance());

        String loggedUser = principal.getName();
        List<Address> addressList = addressService.getAddressOfUser(loggedUser);
        if (addressList.isEmpty()) {
            model.addAttribute("addressList", null);
        } else {
            model.addAttribute("addressList", addressList);

        }


        return "checkout";
    }

    @PostMapping("/checkout/addressAdd")
    public String postCheckout(@Valid @ModelAttribute AddressDTO addressDTO,
                               BindingResult bindingResult, Model model, Principal principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userService.getUserByEmail(currentPrincipalName).orElse(null);

        if (bindingResult.hasErrors()) {
            model.addAttribute("total", Objects.requireNonNull(cartService.findCartByUser
                            (userService.getUserByEmail(principal.getName())
                                    .orElse(null)).orElse(null)).getCartItems()
                    .stream()
                    .map(item -> item.getProduct().getPrice() * item.getQuantity()).reduce(0.0, Double::sum));
            assert user != null;
            Wallet wallet = walletService.getWalletOfUser(user.getId());
            model.addAttribute("walletAmount", wallet.getBalance());

            List<Address> addressList = addressService.getAddressOfUser(user.getEmail());
            if (addressList.isEmpty()) {
                System.out.println(addressList);
                model.addAttribute("addressList", null);
            } else {
                model.addAttribute("addressList", addressList);

            }


            model.addAttribute("addressError", OrderManagementMessages.ADDRESS_ERROR.getMessage());
            return "checkout";
        }

        Address address = new Address();
        address.setId(addressDTO.getId());
        address.setName(addressDTO.getName());
        address.setCity(addressDTO.getCity());
        address.setPhoneNo(addressDTO.getPhoneNo());
        address.setLandmark(addressDTO.getLandmark());
        address.setUser(user);
        address.setPincode(addressDTO.getPincode());
        address.setAddress(addressDTO.getAddress());
        addressService.addAddress(address);

        return "redirect:/checkout";
    }

    @PostMapping("/checkout/confirmOrder")
    public String confirmOrder(@Valid @ModelAttribute("selectedAddress") int id,
                               @ModelAttribute("appliedCoupon") String couponCode,
                               @ModelAttribute("paymentMethod") String paymentMethod,
                               Principal principal,
                               HttpServletRequest servletRequest,
                               RedirectAttributes redirectAttributes, Model model
    ) {
        User user = userService.getUserByEmail(principal.getName()).orElseThrow();
        Cart cart = cartService.findCartByUser(user).orElse(null);
        assert cart != null;
        List<CartItem> cartItemLists = cart.getCartItems();
        for (CartItem cartItem : cartItemLists) {
            Product product = cartItem.getProduct();
            if (product.getQuantity() < cartItem.getQuantity()) {
                redirectAttributes.addFlashAttribute("stockError", "No stock available");
                return "redirect:/checkout";
            }
        }

        double total;

        if (userBooleanMap.get((Objects.requireNonNull(userService.getUserByEmail(principal.getName())
                .orElse(null)))
                .getEmail())) {

            total = userDoubleMap.get(Objects.requireNonNull(userService.getUserByEmail(principal.getName())
                    .orElse(null)).getEmail());
        } else {
            total = Objects.requireNonNull(cartService.findCartByUser(userService.getUserByEmail
                            (principal.getName()).orElse(null)).orElse(null)).getCartItems()
                    .stream().map(item -> item.getProduct().getPrice() * item.getQuantity())
                    .reduce(0.0, Double::sum);
        }


        userBooleanMap.put(Objects.requireNonNull(userService.getUserByEmail(principal.getName())
                .orElse(null)).getEmail(), false);

        Coupon coupon = couponService.getByCouponCode(couponCode);


        if (coupon != null) {
            int quantity = coupon.getUsageLimit() - 1;
            coupon.setUsageLimit(quantity);
            couponService.saveCoupon(coupon);
        }

        if (Objects.equals(paymentMethod, "razorPay")) {

            return handleRazorpayPayment(total, id, principal, model, redirectAttributes, servletRequest);
        }
        if (Objects.equals(paymentMethod, "wallet")) {
            return handleWalletPayment(total, id, principal, redirectAttributes, model);

        } else {
            return handleOtherPaymentMethods(total, id, principal, redirectAttributes, model);
        }


    }

    private String handleWalletPayment(double total, int id, Principal principal,
                                       RedirectAttributes redirectAttributes, Model model) {
        User user = userService.getUserByEmail(principal.getName()).orElse(null);
        Orders orders = new Orders();
        orders.setAddress(addressService.getAddressById(id));
        orders.setUser(user);
        orders.setPaymentMethod(paymentMethodRepository.findById(3L).orElse(null));
        orders.setOrderStatus(orderStatusRepository.findById(1L).orElse(null));
        ZonedDateTime zonedDateTime=istTime();

        orders.setLocalDateTime(zonedDateTime.toLocalDateTime());
        orders.setAmount((int) total);
        orderService.saveOrder(orders);
        long orderId = orders.getId();

        Cart cart = cartService.findCartByUser(user).orElse(null);

        assert cart != null;
        List<CartItem> cartItemLists = cart.getCartItems();
        for (CartItem cartItem : cartItemLists) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            Product product = cartItem.getProduct();
            int qu = product.getQuantity() - cartItem.getQuantity();
            product.setQuantity(qu);
            productRepository.save(product);
            orderItem.setOrders(orders);
            orderItem.setProductVariants(cartItem.getProductVariants());
            orderItemService.saveOrderItem(orderItem);
        }
        assert user != null;
        Wallet wallet = walletService.getWalletOfUser(user.getId());
        if (wallet.getBalance() < total) {
            redirectAttributes.addFlashAttribute("walletError", OrderManagementMessages.WALLET_INSUFFICIENT.getMessage());
            return "redirect:/checkout";
        }
        double newBalance = wallet.getBalance() - total;
        wallet.setBalance(newBalance);
        walletService.saveWallet(wallet);
        cartItemRepository.deleteAll(cartItemLists);

        redirectAttributes.addFlashAttribute("orderId", orderId);
        redirectAttributes.addFlashAttribute("selectedAddress", addressService.getAddressById(id));
        return "redirect:/orderSuccess";
    }


    @GetMapping("/orderSuccess")
    public String getOrderSuccess(Model model, @ModelAttribute("orderId") long orderId) {
        Orders orders = orderService.getOrderById(orderId).orElse(null);
        assert orders != null;
        model.addAttribute("orderItem", orders.getOrderItems());
        model.addAttribute("total", orders.getAmount());
        return "orderSuccess";

    }

    @PostMapping("order/couponCode")
    public String getCoupon(@RequestParam("couponCode") String couponCode, Principal principal,
                            RedirectAttributes redirectAttributes) {

        Coupon coupon = couponService.getByCouponCode(couponCode);
        if (coupon == null) {
            redirectAttributes.addFlashAttribute("errorCoupon", CouponManagementMessages.COUPON_VALID.getMessage());
            return "redirect:/checkout";
        }

        if (coupon.getExpiryDate().isBefore(ChronoLocalDate.from(LocalDateTime.now()))) {

            redirectAttributes.addFlashAttribute("errorCoupon", CouponManagementMessages.COUPON_NOT_VALID.getMessage());
            return "redirect:/checkout";
        }

        if (coupon.getUsageLimit() <= 0) {
            redirectAttributes.addFlashAttribute("errorCoupon", CouponManagementMessages.COUPON_LIMIT.getMessage());
            return "redirect:/checkout";
        }

        double totalDiscount = Objects.requireNonNull(cartService.findCartByUser(userService.getUserByEmail
                        (principal.getName()).orElse(null)).orElse(null)).getCartItems()
                .stream().map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(0.0, Double::sum);

        if (totalDiscount < coupon.getDiscountAmount()) {
            redirectAttributes.addFlashAttribute("errorCoupon",
                    CouponManagementMessages.COUPON_CANNOT_APPLIED.getMessage());
            return "redirect:/checkout";
        }

        int discount = coupon.getDiscountAmount();
        totalDiscount -= discount;
        userBooleanMap.put(Objects.requireNonNull(userService.getUserByEmail(principal.getName())
                .orElse(null)).getEmail(), true);
        userDoubleMap.put(Objects.requireNonNull(userService.getUserByEmail(principal.getName())
                .orElse(null)).getEmail(), totalDiscount);

        redirectAttributes.addFlashAttribute("successCoupon", CouponManagementMessages.COUPON_SUCCESS.getMessage());
        redirectAttributes.addFlashAttribute("totalDiscount", String.valueOf(totalDiscount));
        redirectAttributes.addFlashAttribute("appliedCoupon", coupon.getCouponCode());

        return "redirect:/checkout";

    }

    private String handleRazorpayPayment(double total, int id,
                                         Principal principal, Model model,
                                         RedirectAttributes redirectAttributes, HttpServletRequest
                                                 httpServletRequest) {

        try {
            TransactionDetails transactionDetails = orderService.createTransaction(total);
            String RazorOrderId = transactionDetails.getOrderId();

            HttpSession session = httpServletRequest.getSession();
            session.setAttribute("orderId", RazorOrderId);
            session.setAttribute("amount", total * 100);
            session.setAttribute("username", principal.getName());
            session.setAttribute("address", id);

            // Other Razorpay setup code
            model.addAttribute("orderId", RazorOrderId);
            model.addAttribute("amount", total * 100);
            model.addAttribute("address", id);
            return "razorPayment";
        } catch (RazorpayException e) {
            redirectAttributes.addFlashAttribute("errorCoupon",
                    OrderManagementMessages.WALLET_ERROR.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/orderSuccessful")
    public String razorOrder(HttpSession httpSession, RedirectAttributes redirectAttributes) {

        double total = Double.parseDouble(httpSession.getAttribute("amount").toString());
        String username = (String) httpSession.getAttribute("username");
        int addressId = (int) httpSession.getAttribute("address");


        User user = userService.getUserByEmail(username).orElse(null);
        Orders orders = new Orders();
        orders.setAddress(addressService.getAddressById(addressId));
        orders.setUser(user);
        orders.setPaymentMethod(paymentMethodRepository.findById(2L).orElse(null));
        orders.setOrderStatus(orderStatusRepository.findById(1L).orElse(null));
        orders.setLocalDateTime(LocalDateTime.now());
        orders.setAmount((int) total / 100);
        orderService.saveOrder(orders);
        long orderId = orders.getId();

        Cart cart = cartService.findCartByUser(user).orElse(null);
        assert cart != null;
        List<CartItem> cartItemLists = cart.getCartItems();


        for (CartItem cartItem : cartItemLists) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            Product product = cartItem.getProduct();
            int qu = product.getQuantity() - cartItem.getQuantity();
            product.setQuantity(qu);
            productRepository.save(product);
            orderItem.setProductVariants(cartItem.getProductVariants());
            orderItem.setOrders(orders);
            orderItemService.saveOrderItem(orderItem);
        }


        redirectAttributes.addFlashAttribute("orderId", orderId);
        redirectAttributes.addFlashAttribute("selectedAddress", addressService.getAddressById(addressId));
        return "redirect:/orderSuccess";
    }

    private String handleOtherPaymentMethods(double total, int id, Principal principal,
                                             RedirectAttributes redirectAttributes, Model model) {
        User user = userService.getUserByEmail(principal.getName()).orElse(null);
        Orders orders = new Orders();
        orders.setAddress(addressService.getAddressById(id));
        orders.setUser(user);
        orders.setPaymentMethod(paymentMethodRepository.findById(1L).orElse(null));
        orders.setOrderStatus(orderStatusRepository.findById(1L).orElse(null));

        ZonedDateTime zonedDateTime=istTime();

        orders.setLocalDateTime(zonedDateTime.toLocalDateTime());
        orders.setAmount((int) total);
        orderService.saveOrder(orders);
        long orderId = orders.getId();
        Cart cart = cartService.findCartByUser(user).orElse(null);
        assert cart != null;
        List<CartItem> cartItemLists = cart.getCartItems();


        for (CartItem cartItem : cartItemLists) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            Product product = cartItem.getProduct();
            int qu = product.getQuantity() - cartItem.getQuantity();
            product.setQuantity(qu);
            productRepository.save(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductVariants(cartItem.getProductVariants());
            orderItem.setOrders(orders);
            orderItemService.saveOrderItem(orderItem);
        }
        cartItemRepository.deleteAll(cartItemLists);
        redirectAttributes.addFlashAttribute("orderId", orderId);
        redirectAttributes.addFlashAttribute("selectedAddress", addressService.getAddressById(id));
        return "redirect:/orderSuccess";
    }

    public ZonedDateTime istTime() {
        LocalDateTime localDateTime=LocalDateTime.now();
     return localDateTime.atZone(ZoneId.of("Asia/Kolkata"));
    }


}
