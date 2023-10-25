package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.ProductVariants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantsRepository extends JpaRepository<ProductVariants,Long> {


}
