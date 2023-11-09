package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.BannerImage;
import com.ecommerce.miniproject.repository.BannerImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {
    @Autowired
    BannerImageRepository bannerImageRepository;
    public void deleteBannerById(long id){
        bannerImageRepository.deleteById(id);
    }
    public List<BannerImage> findAllBanners(){
        return bannerImageRepository.findAll();
    }
    public void saveBanner(BannerImage bannerImage){
        bannerImageRepository.save(bannerImage);
    }
}
