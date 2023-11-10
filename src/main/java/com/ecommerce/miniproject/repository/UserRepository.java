package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional <User> findUserByEmail(String email);

    List<User> findByFirstNameContaining(String keyword);

    @Modifying
    @Query("DELETE FROM User u WHERE u.otpActive = false")
    @Transactional
    void deleteNonVerifiedUsers();



}
