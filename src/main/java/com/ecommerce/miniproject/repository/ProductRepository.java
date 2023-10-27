package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
     List<Product> findAllByCategory_id(int id);

    boolean existsByName(String name);

    List<Product> findByNameContaining(String name);

    Boolean existsByCategory_Id(int id);
}
