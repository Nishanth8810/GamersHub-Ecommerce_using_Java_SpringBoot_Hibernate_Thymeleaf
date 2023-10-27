package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.ProductColor;
import com.ecommerce.miniproject.repository.ProductColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductColorService {

    @Autowired
    ProductColorRepository productColorRepository;

  public  ProductColor getProductColorByColor(String color){
      return   productColorRepository.findByColor(color);
    }
}
