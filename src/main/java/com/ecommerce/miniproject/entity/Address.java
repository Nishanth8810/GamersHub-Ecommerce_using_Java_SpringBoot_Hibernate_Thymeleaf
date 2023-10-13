package com.ecommerce.miniproject.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private long phoneNo;

    private int pincode;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNo(long phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    private String address;

    private String city;

    private String landmark;


    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "address")
    private List<Orders> orders;

}
