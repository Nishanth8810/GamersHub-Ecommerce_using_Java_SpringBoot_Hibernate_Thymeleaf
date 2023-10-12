package com.ecommerce.miniproject.global;

import com.ecommerce.miniproject.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {

    public static List<Product> cart;

    static {
        cart=new ArrayList<Product>();
    }

}
