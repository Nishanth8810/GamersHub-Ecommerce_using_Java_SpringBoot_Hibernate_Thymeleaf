package com.ecommerce.miniproject.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ecommerce.miniproject.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StorageService {


    @Value("${application.bucket.name}")
    private String bucketName;
    @Autowired
    private AmazonS3 s3Client;

    public String uploadFile(MultipartFile file) {
        File fileObj = convertMultipartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();
        return fileName;
    }
    private File convertMultipartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
        } catch (IOException e) {
//            log.error("Error converting MultipartFile to File", e);
        }
        return convertedFile;
    }

    public String generateS3ObjectUrl(String imageKey) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, imageKey);
        URL s3ImageUrl = s3Client.generatePresignedUrl(request);
        return s3ImageUrl.toString();
    }

    public List<String> getUrlList(List<Product> products) {
        List<String> urlList = new ArrayList<>();
        for (Product product : products) {
            String url = generateS3ObjectUrl(product.getImageName());
            urlList.add(url);
        }
        return urlList;
    }
//    public List<String> getUrlListOrder(List<Orders> ordersList) {
//        List<String> urlList = new ArrayList<>();
//        for (Orders orders : ordersList) {
//            String url = generateS3ObjectUrl(orders.getOrderItems());
//            urlList.add(url);
//        }
//        return urlList;
//    }
    public List<String> getUrlOrderList(List<Orders> orderList) {
        List<String> urlList = new ArrayList<>();
        for (Orders order : orderList) {
            for (OrderItem orderItem : order.getOrderItems()) {
                urlList.add(generateS3ObjectUrl(orderItem.getProduct().getImageName()));
            }
        }
        return urlList;
    }
    public List<String> getUrlListForSingleProduct(Product product) {
        List<String> urlList = new ArrayList<>();
        urlList.add(generateS3ObjectUrl(product.getImageName()));
        for (ProductImage productImage : product.getProductImages()) {
            urlList.add(generateS3ObjectUrl(productImage.getImageName()));
        }
        return urlList;
    }

    public List<String> getUrlListForSingleOrder(Orders order) {
        List<String> urlList = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            urlList.add(generateS3ObjectUrl(orderItem.getProduct().getImageName()));
        }
        return urlList;
    }

    public List<String> getUrlListForSingleCart(Cart cart) {
        List<String> urlList = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            urlList.add(generateS3ObjectUrl(cartItem.getProduct().getImageName()));
        }
        return urlList;
    }
    public List<String> getUrlListBanner(List<BannerImage> bannerImages) {
        List<String> urlList = new ArrayList<>();
        for (BannerImage bannerImage : bannerImages) {
            String url = generateS3ObjectUrl(bannerImage.getName());
            urlList.add(url);
        }
        return urlList;

    }





}
