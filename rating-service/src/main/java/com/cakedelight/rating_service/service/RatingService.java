package com.cakedelight.rating_service.service;

import com.cakedelight.rating_service.model.Rating;
import com.cakedelight.rating_service.repository.RatingRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }


    // =========================================================
    // ADD RATING
    // =========================================================

    public Rating addRating(Rating rating) {

        return ratingRepository.save(rating);
    }


    // =========================================================
    // GET RATINGS FOR A CAKE
    // =========================================================

    public List<Rating> getRatingsByCake(Long cakeId) {

        return ratingRepository.findByCakeId(cakeId);
    }


    // =========================================================
    // CALCULATE AVERAGE RATING
    // =========================================================

    public double getAverageRating(Long cakeId) {

        List<Rating> ratings =
                ratingRepository.findByCakeId(cakeId);


        if (ratings.isEmpty()) {
            return 0.0;
        }


        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }
}