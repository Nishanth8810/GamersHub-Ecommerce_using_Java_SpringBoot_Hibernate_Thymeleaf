package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.OrderItem;
import com.ecommerce.miniproject.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {
    @Autowired
    OrderItemRepository orderItemRepository;


    public void saveOrderItem(OrderItem orderItem){
        orderItemRepository.save(orderItem);
    }
    public boolean orderItemCheck(long id){
        List<OrderItem> orderItem=orderItemRepository.findByProduct_Id(id);
        if (!orderItem.isEmpty()){
            return true;
        }
        return false;
    }
}
