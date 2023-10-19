package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.Address;
import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.repository.OrderRepository;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AddressService addressService;


    public void saveOrder(Orders orders){
        orderRepository.save(orders);
    }

    public Optional<Orders> getOrderById(long id){
        return orderRepository.findById(id);
    }

    public List<Orders> getAllOrders(){
        return orderRepository.findAll();
    }

    public boolean isAddressUsedInOrder(int id) {
//       Orders orders= orderRepository.findById(id).get();
//        Address address= addressService.getAddressById(id);
        List<Orders> orders = orderRepository.findByAddressId(id);
        if (orders.isEmpty()){
            return false;
        }
        else {
            return true;
        }

    }
}
