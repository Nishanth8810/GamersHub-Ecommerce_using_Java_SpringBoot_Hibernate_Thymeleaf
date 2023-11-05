package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.Orders;
import com.ecommerce.miniproject.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
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
    public List<List<Object>> monthlyReport() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        List<Orders> ordersListMonthly = new ArrayList<>();
        List<Object> xAxis = new ArrayList<>();
        List<Object> yAxis = new ArrayList<>();
        List<Object> yAxis2 = new ArrayList<>();

        while (!firstDayOfMonth.isAfter(today)) {
            ordersListMonthly = orderRepository.findByLocalDateTimeBetween(
                    firstDayOfMonth.with(LocalTime.MIN),
                    lastDayOfMonth.with(LocalTime.MAX)
            );
            String dateString = firstDayOfMonth.toLocalDate().toString();
            xAxis.add(dateString);
            yAxis.add(ordersListMonthly.size());
            yAxis2.add(ordersListMonthly.stream().mapToInt(Orders::getAmount).sum());
            firstDayOfMonth = firstDayOfMonth.plusMonths(1);
        }

        return List.of(xAxis, yAxis);
    }
    public List<List<Object>> yearlyReport() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDateTime lastDayOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        List<Orders> ordersListYearly = new ArrayList<>();
        List<Object> xAxis = new ArrayList<>();
        List<Object> yAxis = new ArrayList<>();
        List<Object> yAxis2 = new ArrayList<>();

        while (!firstDayOfYear.isAfter(today)) {
            ordersListYearly = orderRepository.findByLocalDateTimeBetween(
                    firstDayOfYear.with(LocalTime.MIN),
                    lastDayOfYear.with(LocalTime.MAX)
            );
            String dateString = String.valueOf(firstDayOfYear.getYear());
            xAxis.add(dateString);
            yAxis.add(ordersListYearly.size());
            yAxis2.add(ordersListYearly.stream().mapToInt(Orders::getAmount).sum());
            firstDayOfYear = firstDayOfYear.plusMonths(1);
        }

        return List.of(xAxis, yAxis);
    }

}
