package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.ProductSize;
import com.ecommerce.miniproject.entity.ProductVariants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductVariantsRepository extends JpaRepository<ProductVariants,Integer> {

//    List<ProductVariants> findAllByProductSize(ProductSize productSize);
    @Query("SELECT DISTINCT pv.productSize FROM ProductVariants pv")
    List<ProductSize> findAllProductSizes();

    ProductVariants findById(int id);

  Optional<ProductVariants> findByProductId(int id);


}
