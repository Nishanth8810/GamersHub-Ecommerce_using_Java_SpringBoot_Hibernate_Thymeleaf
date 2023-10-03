package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    public List<Product> findAllByCategory_id(int id);
}
