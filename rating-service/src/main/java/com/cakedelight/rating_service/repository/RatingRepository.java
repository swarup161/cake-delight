package com.cakedelight.rating_service.repository;

import com.cakedelight.rating_service.model.Rating;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository
        extends JpaRepository<Rating, Long> {

    // Get all ratings for a particular cake
    List<Rating> findByCakeId(Long cakeId);
}