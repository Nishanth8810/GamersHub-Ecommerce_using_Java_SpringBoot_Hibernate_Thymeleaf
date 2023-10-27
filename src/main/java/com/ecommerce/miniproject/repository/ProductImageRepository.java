package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    void deleteAllByProductId(long id);
}
