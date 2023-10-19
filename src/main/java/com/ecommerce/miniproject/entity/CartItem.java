package com.ecommerce.miniproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Getter
@Entity

public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    private int quantity;

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", product=" + product +
                ", quantity=" + quantity +
                ", cart=" + cart +
                '}';
    }

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }


}
