package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.repository.ProductRepository;
import com.ecommerce.miniproject.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        return productRepository.existsByName(name);
    }
    public Page<Product> findPaginated(int pageNo, int pageSize){
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return this.productRepository.findAll(pageable);
    }


    public List<Product> searchProductsByKeyword(String keyword) {

        return productRepository.findByNameContaining(keyword);
    }

    public List<Product> findByName(String keyword) {
       return productRepository.findByNameContaining(keyword);
    }

    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
      return   productRepository.findByPriceBetween(minPrice,maxPrice);
    }
}