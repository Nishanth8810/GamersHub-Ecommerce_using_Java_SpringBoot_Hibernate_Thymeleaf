package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus,Long> {
}
