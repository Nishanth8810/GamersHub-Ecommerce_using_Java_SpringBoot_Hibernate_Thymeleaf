package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.OrderItem;
import com.ecommerce.miniproject.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    @Autowired
    OrderItemRepository orderItemRepository;
    public void saveOrderItem(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }

    public boolean orderItemCheck(long id) {
        return orderItemRepository.existsByProduct_Id(id);
    }
}
