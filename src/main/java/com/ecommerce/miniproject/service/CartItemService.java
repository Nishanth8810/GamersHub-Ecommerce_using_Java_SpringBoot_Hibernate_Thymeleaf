package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    @Autowired
    CartItemRepository cartItemRepository;

    public void removeCartItemOfUser(long id){
        cartItemRepository.deleteById(id);
    }
}
