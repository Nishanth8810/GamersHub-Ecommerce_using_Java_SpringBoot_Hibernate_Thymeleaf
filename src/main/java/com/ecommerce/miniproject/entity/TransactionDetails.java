package com.ecommerce.miniproject.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionDetails {

    private String orderId;

    private String currency;
    private Integer amount;

    public TransactionDetails(String orderId, String currency, Integer amount) {
        this.orderId = orderId;
        this.currency = currency;
        this.amount = amount;
    }


}
