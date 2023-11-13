package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    boolean existsByAddressId(int id);

    Orders findById(int id);

    List<Orders> findByLocalDateTimeBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);

    Orders findFirstByOrderByLocalDateTimeAsc();

    List<Orders> findByUserId(Integer id);
}
