package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.OrderItem;
import com.ecommerce.miniproject.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderItemService {
    @Autowired
    OrderItemRepository orderItemRepository;


    public void saveOrderItem(OrderItem orderItem){
        orderItemRepository.save(orderItem);
    }
    public Optional<OrderItem> getOrderItemByOrderId(Long id){
        return orderItemRepository.findByOrders_Id(id);
    }
}
