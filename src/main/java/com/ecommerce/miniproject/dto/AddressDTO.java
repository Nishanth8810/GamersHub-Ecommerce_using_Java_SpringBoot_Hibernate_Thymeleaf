package com.ecommerce.miniproject.dto;

import com.ecommerce.miniproject.entity.User;
import lombok.Data;

@Data
public class AddressDTO {

    private int id;
    private String name;
    private long phoneNo;
    private int pincode;
    private String address;
    private String city;
    private String landmark;
    private int alternateNo;
    private User user;

}
