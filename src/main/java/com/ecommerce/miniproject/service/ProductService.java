package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.repository.ProductRepository;
import com.ecommerce.miniproject.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public List<Product> getAllProduct() {
        return productRepository.findAll();

    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public void removeProductById(long id) {
        productRepository.deleteById(id);
    }

    public Optional<Product> getProductById(long id) {
        return productRepository.findById(id);
    }
    public List<Product> getAllProductsByCategory_id(int id){
        return  productRepository.findAllByCategory_id(id);
    }

    public boolean getProductByName(String name){
        Optional<Product> optionalProduct = productRepository.getProductByName(name);
        if (optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            return true;
        }
        else{
            return false;
        }

    }

    public List<Product> getAllProducts(PageRequest of) {
      return   productRepository.findAll();
    }
}