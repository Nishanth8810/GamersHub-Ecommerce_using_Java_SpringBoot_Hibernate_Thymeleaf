package com.ecommerce.miniproject.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@Setter
@Component
public class Report {

    private Long orderId;

    private LocalDateTime orderDate;

    private String customer;

    private int totalAmount;

    private String paymentMethod;

    private String products;

    private String orderStatus;
}