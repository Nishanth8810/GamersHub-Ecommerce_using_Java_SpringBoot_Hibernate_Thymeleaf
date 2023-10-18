package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.AddressDTO;
import com.ecommerce.miniproject.entity.Address;
import com.ecommerce.miniproject.entity.Cart;
import com.ecommerce.miniproject.entity.CartItem;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.CartItemRepository;
import com.ecommerce.miniproject.repository.CartRepository;
import com.ecommerce.miniproject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.security.Principal;
import java.util.List;
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



    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable int id, Principal principal){

        User user = userService.getUserByEmail(principal.getName()).get();
        Cart cart = cartService.findCartByUser(user).get();
        List<CartItem> cartItemList = cart.getCartItems();
        Optional<CartItem> cartItemOptional = cartItemRepository.findCartItemByProductAndCart(productService.getProductById(id).get(), cart);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(productService.getProductById(id).get());
            cartItem.setCart(cart);
            cartItem.setQuantity(1);
            cartItemRepository.save(cartItem);
        }
        return "redirect:/shop/viewproduct/" + id;

    }
    @GetMapping("/cart")
    public String cartGet(Model model, Principal principal){
        int number=cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().size();
        if (number==0){
            return "CartEmpty";
        }

        model.addAttribute("cartCount", cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().size());
        model.addAttribute("total",cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().stream().map(item->item.getProduct().getPrice()*item.getQuantity()).reduce(0.0, (a, b) -> a + b));
        model.addAttribute("cart", cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems());
        return "cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal){

       int number=cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().size();
       if (number==0){
           return "redirect:/cart";
       }

        model.addAttribute("total",cartService.findCartByUser(userService.getUserByEmail(principal.getName()).get()).get().getCartItems().stream().map(item->item.getProduct().getPrice()*item.getQuantity()).reduce(0.0, (a, b) -> a + b));
        model.addAttribute("addressDTO",new AddressDTO());

       String loggedUser= principal.getName();
        List<Address>addressList=addressService.getAddressOfUser(loggedUser);
        model.addAttribute("addressList",addressList);


        return "checkout";
    }


    @PostMapping("/checkout")
    public String postCheckout(@ModelAttribute AddressDTO addressDTO) {



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userService.getUserByEmail(currentPrincipalName).get();

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
         public String removeCart(@PathVariable long id){
        cartItemService.removeCartItemOfUser(id);

        return "redirect:/cart";


        }

        @GetMapping("/cart/increaseQuantity/{id}")
        public String getIncreaseQuantity(@PathVariable long id){
        CartItem cartItem = cartItemRepository.findById(id).get();

        cartItem.setQuantity(cartItem.getQuantity()+1);
            cartItemRepository.save(cartItem);

            return "redirect:/cart";
        }

         @GetMapping("/cart/decreaseQuantity/{id}")
         public String getDecreaseQuantity(@PathVariable long id){

             CartItem cartItem = cartItemRepository.findById(id).get();
             long quantity=cartItem.getQuantity();
             if (quantity>1){

                 cartItem.setQuantity(cartItem.getQuantity()-1);
                 cartItemRepository.save(cartItem);
                     return "redirect:/cart";
                 } else if (quantity==1){
                 cartItemService.removeCartItemOfUser(id);
             }
             return "redirect:/cart";
    }

}
