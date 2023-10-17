package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class OrderService {

    @Autowired
    OrderRepository orderRepository;

}
