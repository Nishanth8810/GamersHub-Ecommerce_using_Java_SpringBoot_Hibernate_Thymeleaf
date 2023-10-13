package com.ecommerce.miniproject.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private long phoneNo;

    private int pincode;

    private String address;

    private String city;

    private String landmark;


    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "address")
    private List<Orders> orders;

}
