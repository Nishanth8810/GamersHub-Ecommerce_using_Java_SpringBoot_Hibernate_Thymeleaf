package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    boolean existsByProduct_Id(Long id);
}
