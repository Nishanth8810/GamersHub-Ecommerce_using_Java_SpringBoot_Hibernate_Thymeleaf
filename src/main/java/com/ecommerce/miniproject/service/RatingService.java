package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class RatingService {
    @Autowired
    RatingRepository ratingRepository;
    public boolean isReviewExists(long productId,long userId){
        return ratingRepository.existsByProduct_IdAndUser_Id(productId,userId);
    }


}
