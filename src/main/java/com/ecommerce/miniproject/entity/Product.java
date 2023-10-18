package com.ecommerce.miniproject.entity;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(min = 1, message = "Name is required")
    private String name;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id" ,referencedColumnName = "category_id")
    private Category category;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "product_variant")
    private ProductVariants productVariants;


    @OneToMany(mappedBy = "product")
    private List<Orders> orders;

    private int quantity;
    private double price;
    private double weight;
    private String description;
    private String imageName;
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ProductImage> images = new ArrayList<>();

}
