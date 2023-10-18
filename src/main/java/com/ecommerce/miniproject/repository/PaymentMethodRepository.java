package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Long> {
}
