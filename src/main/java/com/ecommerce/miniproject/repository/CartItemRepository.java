package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Cart;
import com.ecommerce.miniproject.entity.CartItem;
import com.ecommerce.miniproject.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
     List<CartItem> findCartItemByProductAndCart(Product product, Cart cart);
}
