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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class OrderService {

    private static final String KEY = "rzp_test_56kwMexaivSGfE";
    private static final String KEY_SECRET = "68dL8y42wwAwnGpC1phaxsMl";

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

    public TransactionDetails prepareTransactionDetails(Order order) {
        String orderId = order.get("id");
        String currency = order.get("currency");
        Integer amount = order.get("amount");

        return new TransactionDetails(orderId, currency, amount);
    }

    public int getOrderCount() {
        List<Orders> order = orderRepository.findAll();
        return order.size();
    }

    ////////sales report////////

    public HashMap<String, Integer> todayOrderCount() {
        LocalDateTime today = LocalDateTime.now();
        List<Orders> ordersList = orderRepository.findByLocalDateTimeBetween(today.with(LocalTime.MIN), today.with(LocalTime.MAX));
        int sum = ordersList.stream().mapToInt(Orders::getAmount).sum();
        HashMap<String, Integer> summary = new HashMap<>();
        summary.put("orderCount", ordersList.size());
        summary.put("revenue", sum);
        return summary;
    }
    public HashMap<String, Integer> weeklyOrderCount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1).plusDays(1);
        List<Orders> ordersList = orderRepository.findByLocalDateTimeBetween(oneWeekAgo.with(LocalTime.MIN), now.with(LocalTime.MAX));
        int sum = ordersList.stream().mapToInt(Orders::getAmount).sum();
        HashMap<String, Integer> summary = new HashMap<>();
        summary.put("weeklyOrderCount", ordersList.size());
        summary.put("weeklyRevenue", sum);
        return summary;
    }
    public HashMap<String, Integer> monthlyOrderCount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusMonths(1);
        List<Orders> ordersList = orderRepository.findByLocalDateTimeBetween(oneWeekAgo.with(LocalTime.MIN), now.with(LocalTime.MAX));
        int sum = ordersList.stream().mapToInt(Orders::getAmount).sum();
        HashMap<String, Integer> summary = new HashMap<>();
        summary.put("monthlyOrderCount", ordersList.size());
        summary.put("monthlyRevenue", sum);
        return summary;
    }
    public HashMap<String, Integer> yearlyOrderCount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusYears(1);
        List<Orders> ordersList = orderRepository.findByLocalDateTimeBetween(oneWeekAgo.with(LocalTime.MIN), now.with(LocalTime.MAX));
        int sum = ordersList.stream().mapToInt(Orders::getAmount).sum();
        HashMap<String, Integer> summary = new HashMap<>();
        summary.put("yearlyOrderCount", ordersList.size());
        summary.put("yearlyRevenue", sum);
        return summary;
    }
}
