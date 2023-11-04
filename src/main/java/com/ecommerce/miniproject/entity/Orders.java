package com.ecommerce.miniproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Entity
public class Orders {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private int amount;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;


    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;


    @Column(name = "order_placed_time")
    private LocalDateTime localDateTime;


    @ManyToOne
    private OrderStatus orderStatus;

    public boolean isNotCancelled() {
        return !"order cancelled".equalsIgnoreCase(orderStatus.getStatus());
    }

    public boolean isNotReturned(){
        return !"order returned".equalsIgnoreCase(orderStatus.getStatus());
    }
    public boolean isNotDelivered(){
        return !"order delivered".equalsIgnoreCase(orderStatus.getStatus());
    }











}
