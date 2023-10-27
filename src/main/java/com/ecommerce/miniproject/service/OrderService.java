package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.entity.TransactionDetails;
import com.ecommerce.miniproject.repository.OrderRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class OrderService {

    private static final String KEY = "rzp_test_6yikyjM4VI0lBk";
    private static final String KEY_SECRET = "U3tFVg9E4NV8nwPuSN5mFji6";

    private static final String CURRENCY = "INR";

    @Autowired
    OrderRepository orderRepository;


    public void saveOrder(Orders orders) {
        orderRepository.save(orders);
    }

    public Optional<Orders> getOrderById(long id) {
        return orderRepository.findById(id);
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    public boolean isAddressUsedInOrder(int id) {
        return orderRepository.existsByAddressId(id);
    }

    public TransactionDetails createTransaction(Double amount) throws RazorpayException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amount * 100);
        jsonObject.put("currency", CURRENCY);
        try {
            RazorpayClient razorpayClient = new RazorpayClient(KEY, KEY_SECRET);
            Order order = razorpayClient.orders.create(jsonObject);
            return prepareTransactionDetails(order);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public TransactionDetails prepareTransactionDetails(Order order){
        String orderId=order.get("id");
        String currency=order.get("currency");
        Integer amount=order.get("amount");

        return new TransactionDetails(orderId,currency,amount);
    }
}
