package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.repository.BannerImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BannerService {
    @Autowired
    BannerImageRepository bannerImageRepository;
}
