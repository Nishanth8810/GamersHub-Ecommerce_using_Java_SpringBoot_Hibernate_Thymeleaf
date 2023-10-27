package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductColorRepository extends JpaRepository<ProductColor,Long> {
    ProductColor findByColor(String name);
}
