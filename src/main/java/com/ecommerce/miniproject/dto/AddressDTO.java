package com.ecommerce.miniproject.dto;

import com.ecommerce.miniproject.entity.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDTO {

    private int id;
    @Size(min = 1,message = "Name is required")
    private String name;
    @Min(message = "Phone must be a 10 digit number",value = 10)
    private long phoneNo;

    @Min(message = "Pincode is required",value = 6)
    private int pincode;

    @Size(min = 1,message = "Address is required")
    private String address;

    @Size(min = 1,message = "City id required")
    private String city;

    @Size(min = 1,message = "Land Mark id required")
    private String landmark;

    private int alternateNo;

    private User user;

}
