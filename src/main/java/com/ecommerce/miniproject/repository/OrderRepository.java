package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Orders;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderRepository extends JpaRepository<Orders,Long> {

    boolean existsByAddressId(int id);
    Orders findById(int id);
//    @Query("SELECT DATE(o.orderDate) AS orderDate, COUNT(o.id) AS orderCount FROM Orders o GROUP BY DATE(o.orderDate)")
//    List<Map<String, Object>> getOrderCountsByDate();

  List  <Orders> findByLocalDateTimeBetween(LocalDateTime localDateTime,LocalDateTime localDateTime1);

}
