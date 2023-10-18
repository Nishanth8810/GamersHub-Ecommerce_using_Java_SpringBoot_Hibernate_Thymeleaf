package com.ecommerce.miniproject.dto;

import lombok.Data;

@Data
public class OrdersDTO {
        private Long id;
        private int quantity;
        private int amount;
        private String payment;
        private Long userId;
        private Long productId;
        private Long addressId;

}
