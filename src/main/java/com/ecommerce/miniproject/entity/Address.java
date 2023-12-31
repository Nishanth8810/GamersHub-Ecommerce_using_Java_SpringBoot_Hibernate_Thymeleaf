package com.ecommerce.miniproject.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
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

    private boolean isDefault;

    public boolean isDefault(){
        return isDefault;
    }
    public String getFullAddress(){
        return
                 id +
                " " + name + '\'' +
                " " + phoneNo +
                " " + pincode +
                " "+ address + '\'' ;

    }


    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNo=" + phoneNo +
                ", pincode=" + pincode +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", landmark='" + landmark + '\'' +
                ", user=" + user +
                ", orders=" + orders +
                '}';
    }


}
