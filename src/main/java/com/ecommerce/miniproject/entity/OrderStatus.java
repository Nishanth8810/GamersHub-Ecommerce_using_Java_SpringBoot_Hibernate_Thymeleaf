package com.ecommerce.miniproject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String status;

    @ManyToOne
    private Orders order;



}
