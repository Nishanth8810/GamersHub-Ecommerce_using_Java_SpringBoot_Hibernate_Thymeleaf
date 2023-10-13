package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
