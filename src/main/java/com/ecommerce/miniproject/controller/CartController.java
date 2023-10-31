package com.ecommerce.miniproject.controller;
import com.ecommerce.miniproject.dto.AddressDTO;
import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.enums.CartManagementMessages;
import com.ecommerce.miniproject.repository.CartItemRepository;
import com.ecommerce.miniproject.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class CartController {
    @Autowired
    ProductService productService;

    @Autowired
    AddressService addressService;
    @Autowired
    UserService userService;

    @Autowired
    CartService cartService;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    CartItemService cartItemService;

    @Autowired
    OrderController orderController;
    @Autowired
    ProductVariantsService productVariantsService;

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam("selectedVariantSize") String selectedSize,
                            @RequestParam("selectedVariantColor") String selectedColor,
                            @RequestParam("productId") int productId,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {

        if (!Objects.equals(selectedColor, selectedSize)){
            redirectAttributes.addFlashAttribute("errorVariant",
                    CartManagementMessages.ERROR_VARIANT.getMessage());
            return "redirect:/shop/viewProduct/" + productId;
        }

        User user = userService.getUserByEmail(principal.getName()).orElseThrow();
        Cart cart = cartService.findCartByUser(user).orElseThrow();
        Optional<CartItem> cartItemOptional = cartItemRepository.findCartItemByProductAndCart
                (productService.getProductById(productId).orElseThrow(), cart);

        if (cartItemOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("alreadyPresent",
                    CartManagementMessages.ALREADY_PRESENT.getMessage());

        } else {
            if (!selectedColor.isEmpty()){
                ProductVariants productVariants=productVariantsService.getVariantById(Integer.parseInt(selectedColor));
                CartItem cartItem = new CartItem();
                cartItem.setProduct(productService.getProductById(productId).get());
                cartItem.setProductVariants(productVariants);
                cartItem.setCart(cart);
                cartItem.setQuantity(1);
                cartItemRepository.save(cartItem);
                redirectAttributes.addFlashAttribute("addedToCart",CartManagementMessages.ADDED_CART.getMessage());
            }else {
                CartItem cartItem = new CartItem();
                cartItem.setProduct(productService.getProductById(productId).orElseThrow());
                cartItem.setCart(cart);
                cartItem.setQuantity(1);
                cartItemRepository.save(cartItem);
                redirectAttributes.addFlashAttribute("addedToCart",CartManagementMessages.ADDED_CART.getMessage());
            }
        }
        return "redirect:/shop/viewProduct/" + productId;
    }

////adding address in checkout///////////
    @PostMapping("/checkout")
    public String postCheckout(@Valid @ModelAttribute AddressDTO addressDTO,
                               BindingResult bindingResult) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userService.getUserByEmail(currentPrincipalName).orElse(null);

        if (bindingResult.hasErrors()) {
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

    @GetMapping("/cart/removeItem/{id}")
    public String removeCart(@PathVariable long id) {
        cartItemService.removeCartItemOfUser(id);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cartGet(Model model, Principal principal) {

        List<User> userList = userService.getAllUser();
        for (User user : userList) {
            orderController.userBooleanMap.put(user.getEmail(), false);
            orderController.userDoubleMap.put(user.getEmail(), 0.0);
        }

        int number = Objects.requireNonNull(cartService.findCartByUser
                (userService.getUserByEmail(principal.getName())
                        .orElse(null)).orElse(null)).getCartItems().size();
        if (number == 0) {
            return "CartEmpty";
        }

        model.addAttribute("cartCount", cartService.findCartByUser
                (userService.getUserByEmail(principal.getName())
                        .get()).get().getCartItems().size());

        model.addAttribute("total", cartService.findCartByUser
                        (userService.getUserByEmail(principal.getName()).get())
                .get().getCartItems()
                .stream()
                .map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(0.0, Double::sum));

        model.addAttribute("cart", cartService.findCartByUser
                (userService.getUserByEmail(principal.getName()).get()).get().getCartItems());
        return "cart";
    }

    @GetMapping("/cart/increaseQuantity/{id}")
    public String getIncreaseQuantity(@PathVariable long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElse(null);

        assert cartItem != null;
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        cartItemRepository.save(cartItem);

        return "redirect:/cart";
    }

    @GetMapping("/cart/decreaseQuantity/{id}")
    public String getDecreaseQuantity(@PathVariable long id) {

        CartItem cartItem = cartItemRepository.findById(id).orElse(null);
        assert cartItem != null;
        long quantity = cartItem.getQuantity();
        if (quantity > 1) {

            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartItemRepository.save(cartItem);
            return "redirect:/cart";
        } else if (quantity == 1) {
            cartItemService.removeCartItemOfUser(id);
        }
        return "redirect:/cart";
    }

}
