package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.OrderItem;
import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.entity.Product;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    boolean existsByProduct_Id(Long id);
}
