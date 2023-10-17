package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders,Long> {

}
