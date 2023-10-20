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
        Optional<Product> optionalProduct = productRepository.getProductByName(name);
        if (optionalProduct.isPresent()){
            Product product = optionalProduct.get();
            return true;
        }
        else{
            return false;
        }
    }
    public Page<Product> findPaginated(int pageNo, int pageSize){
        Pageable pageable= PageRequest.of(pageNo-1,pageSize);
        return this.productRepository.findAll(pageable);
    }
//
//    public List<Product> searchProduct(String keyword){
//
//       List<Product> searchResult=new ArrayList<>();
//       List<Product> allProducts= productRepository.findAll();
//        for (Product product: allProducts) {
//            if (product.getName().toLowerCase().contains(keyword.toLowerCase())){
//                searchResult.add(product);
//            }
//
//        }
//        return searchResult;
//
//    }

    public List<Product> searchProductsByKeyword(String keyword) {

        return productRepository.findByNameContaining(keyword);
    }
}