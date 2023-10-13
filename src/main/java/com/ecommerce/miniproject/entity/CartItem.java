package com.ecommerce.miniproject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    private int quantity;


    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

}
