package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Cart;
import com.ecommerce.miniproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findCartByUser(User user);


}
