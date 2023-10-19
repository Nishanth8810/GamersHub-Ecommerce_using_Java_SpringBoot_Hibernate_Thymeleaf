package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Long> {

    List<Orders> findByAddressId(int id);
}
