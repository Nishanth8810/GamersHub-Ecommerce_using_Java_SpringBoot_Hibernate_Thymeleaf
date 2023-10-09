package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.RoleRepository;
import com.ecommerce.miniproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    public List<User> getAllUser(){

        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id){

        return userRepository.findById(id);

    }
    public void saveUser(User user){
        userRepository.save(user);
    }

    public void removeUserById(int id){
        userRepository.deleteById(id);
    }

    public Optional<User> getUserByEmail(String email)throws NoSuchElementException {
        return userRepository.findUserByEmail(email);

    }






}
