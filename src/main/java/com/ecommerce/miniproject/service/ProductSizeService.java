package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.ProductSize;
import com.ecommerce.miniproject.repository.ProductSizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductSizeService {
    @Autowired
    ProductSizeRepository productSizeRepository;

    public ProductSize getSizeBySize(String size){
        return productSizeRepository.findBySize(size);
    }
}