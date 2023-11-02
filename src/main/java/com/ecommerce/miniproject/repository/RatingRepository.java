package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating,Long> {
}
