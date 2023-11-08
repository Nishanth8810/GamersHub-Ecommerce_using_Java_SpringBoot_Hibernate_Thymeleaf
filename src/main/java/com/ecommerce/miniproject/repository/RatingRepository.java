package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating,Long> {
    List<Rating> findByProductId(long id);
    Boolean existsByProduct_IdAndUser_Id(Long productId, Long userId);
}
