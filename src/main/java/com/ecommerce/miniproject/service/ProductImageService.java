package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductImageService {
    @Autowired
    ProductImageRepository productImageRepository;
@Transactional
    public void removeImageById(long id){
        productImageRepository.deleteAllByProductId(id);
    }

}

