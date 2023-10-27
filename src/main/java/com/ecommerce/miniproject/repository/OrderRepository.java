package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Orders;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Long> {

    boolean existsByAddressId(int id);
    Orders findById(int id);
}
