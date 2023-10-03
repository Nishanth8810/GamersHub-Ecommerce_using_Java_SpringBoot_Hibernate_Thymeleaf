package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional <User> findUserByEmail(String email);
}
