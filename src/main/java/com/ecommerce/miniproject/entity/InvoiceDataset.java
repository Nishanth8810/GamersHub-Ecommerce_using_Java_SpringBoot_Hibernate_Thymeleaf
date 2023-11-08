package com.ecommerce.miniproject.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceDataset {

    private String productName;

    private Integer quantity;

    private Double unitPrice;

    private Double stotal;


}
