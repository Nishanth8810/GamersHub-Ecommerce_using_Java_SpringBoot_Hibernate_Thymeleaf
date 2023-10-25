package com.ecommerce.miniproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class ProductVariants {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

    @Column
    private  String size;

    @Column
    private String color;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


}
