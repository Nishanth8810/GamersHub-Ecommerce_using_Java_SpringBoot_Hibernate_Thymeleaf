package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class ChartService {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;


    public List<List<Object>> weeklyReport(){
        LocalDateTime today=LocalDateTime.now();
        LocalDateTime firstDayOfWeek=today.minusDays(6);
        LocalDateTime lastDayOfWeek=today;
        List<Orders>ordersListWeekly=new ArrayList<>();
        List<Object> xAsis = new ArrayList<>();
        List<Object> yAxis = new ArrayList<>();
        List<Object> yAxis2 = new ArrayList<>();

        while (lastDayOfWeek.toLocalDate().plusDays(1).isAfter(orderRepository.findFirstByOrderByLocalDateTimeAsc().getLocalDateTime().toLocalDate())){
          ordersListWeekly=(orderRepository.findByLocalDateTimeBetween(firstDayOfWeek.with(LocalTime.MIN),lastDayOfWeek.with(LocalTime.MAX)));
          String dateString = firstDayOfWeek.toLocalDate().toString() + " TO " + lastDayOfWeek.toLocalDate().toString();
          xAsis.add(dateString);
          yAxis.add(ordersListWeekly.size());
          yAxis2.add(ordersListWeekly.stream().map(Orders::getAmount).reduce(0, Integer::sum));
            lastDayOfWeek=firstDayOfWeek.minusDays(1);
            firstDayOfWeek=firstDayOfWeek.minusDays(7);
        }

        return List.of(xAsis, yAxis);
    }

}
