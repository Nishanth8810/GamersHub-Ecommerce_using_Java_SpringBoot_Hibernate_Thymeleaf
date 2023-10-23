package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Integer> {


    Boolean existsByName(String name);
}
