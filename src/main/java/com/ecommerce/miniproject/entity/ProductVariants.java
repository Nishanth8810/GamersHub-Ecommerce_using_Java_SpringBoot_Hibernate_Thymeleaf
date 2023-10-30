package com.ecommerce.miniproject.entity;

import jakarta.annotation.Nullable;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "color_id")
    private ProductColor productColor;
    @ManyToOne
    @Nullable
    @JoinColumn(name = "size_id")
    private ProductSize productSize;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


}
