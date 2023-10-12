package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Address;
import com.ecommerce.miniproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Integer> {

    List<Address> findByUser(User user);
}
