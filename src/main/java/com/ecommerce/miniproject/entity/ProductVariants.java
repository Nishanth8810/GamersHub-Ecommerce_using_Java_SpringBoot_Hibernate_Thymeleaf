package com.ecommerce.miniproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class ProductVariants {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

    @Column
    private  String size;

    @Column
    private String color;

    @OneToMany(mappedBy = "productVariants")
    private List<Product> products;


}
