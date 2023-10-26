package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSizeRepository extends JpaRepository<ProductSize,Long> {

    ProductSize findBySize(String size);
}
