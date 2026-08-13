package com.cakedelight.rating_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "ratings")
public class Rating {

    // =========================================================
    // RATING ID
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // CAKE ID
    // =========================================================

    @NotNull
    private Long cakeId;


    // =========================================================
    // RATING
    // =========================================================

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;


    // =========================================================
    // COMMENT
    // =========================================================

    private String comment;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public Rating() {
    }


    // =========================================================
    // GET ID
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    // =========================================================
    // GET CAKE ID
    // =========================================================

    public Long getCakeId() {
        return cakeId;
    }

    public void setCakeId(Long cakeId) {
        this.cakeId = cakeId;
    }


    // =========================================================
    // GET RATING
    // =========================================================

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }


    // =========================================================
    // GET COMMENT
    // =========================================================

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}