package com.cakedelight.catalog.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cakedelight.catalog.model.Cake;

public interface CakeRepository extends JpaRepository<Cake, Long> {

    List<Cake> findByNameContainingIgnoreCase(String name);

    List<Cake> findByCategoryIgnoreCase(String category);

    List<Cake> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Cake> findByNameContainingIgnoreCaseAndCategoryIgnoreCase(
            String name,
            String category);

    List<Cake> findByNameContainingIgnoreCaseAndPriceBetween(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice);

    List<Cake> findByCategoryIgnoreCaseAndPriceBetween(
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice);

    List<Cake> findByNameContainingIgnoreCaseAndCategoryIgnoreCaseAndPriceBetween(
            String name,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice);
}