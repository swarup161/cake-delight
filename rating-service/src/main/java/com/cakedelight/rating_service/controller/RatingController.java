package com.cakedelight.rating_service.controller;

import com.cakedelight.rating_service.model.Rating;
import com.cakedelight.rating_service.service.RatingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // =========================================================
    // ADD RATING
    // =========================================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Rating addRating(
            @Valid @RequestBody Rating rating) {

        return ratingService.addRating(rating);
    }

    // =========================================================
    // GET RATINGS FOR A CAKE
    // =========================================================

    @GetMapping("/cake/{cakeId}")
    public List<Rating> getRatings(
            @PathVariable Long cakeId) {

        return ratingService.getRatingsByCake(cakeId);
    }

    // =========================================================
    // GET AVERAGE RATING
    // =========================================================

    @GetMapping("/cake/{cakeId}/average")
    public Map<String, Object> getAverageRating(
            @PathVariable Long cakeId) {

        double average =
                ratingService.getAverageRating(cakeId);

        return Map.of(
                "cakeId", cakeId,
                "averageRating", average
        );
    }
}