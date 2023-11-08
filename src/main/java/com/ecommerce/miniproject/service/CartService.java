package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.Cart;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    @Autowired
    CartRepository cartRepository;

    public Optional<Cart> findCartByUser(User user) {
        return cartRepository.findCartByUser(user);
    }
    public void save(Cart cart) {
        cartRepository.save(cart);
    }
    public void removeCartItemOfUser(long id) {
        cartRepository.deleteById(id);
    }
    public void removeCartById(Long id) {
        cartRepository.deleteById(id);
    }
}
