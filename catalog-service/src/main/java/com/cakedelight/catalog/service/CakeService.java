package com.cakedelight.catalog.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cakedelight.catalog.model.Cake;
import com.cakedelight.catalog.repository.CakeRepository;

@Service
public class CakeService {

    private final CakeRepository cakeRepository;

    public CakeService(CakeRepository cakeRepository) {
        this.cakeRepository = cakeRepository;
    }

    // Get all cakes
    public List<Cake> getAllCakes(
            String name,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        boolean hasName = name != null && !name.isBlank();
        boolean hasCategory = category != null && !category.isBlank();
        boolean hasMinPrice = minPrice != null;
        boolean hasMaxPrice = maxPrice != null;

        // No filters
        if (!hasName && !hasCategory && !hasMinPrice && !hasMaxPrice) {
            return cakeRepository.findAll();
        }

        // Name + Category + Price range
        if (hasName && hasCategory && hasMinPrice && hasMaxPrice) {
            return cakeRepository
                    .findByNameContainingIgnoreCaseAndCategoryIgnoreCaseAndPriceBetween(
                            name, category, minPrice, maxPrice);
        }

        // Name + Category
        if (hasName && hasCategory) {
            return cakeRepository
                    .findByNameContainingIgnoreCaseAndCategoryIgnoreCase(
                            name, category);
        }

        // Name + Price range
        if (hasName && hasMinPrice && hasMaxPrice) {
            return cakeRepository
                    .findByNameContainingIgnoreCaseAndPriceBetween(
                            name, minPrice, maxPrice);
        }

        // Category + Price range
        if (hasCategory && hasMinPrice && hasMaxPrice) {
            return cakeRepository
                    .findByCategoryIgnoreCaseAndPriceBetween(
                            category, minPrice, maxPrice);
        }

        // Name only
        if (hasName) {
            return cakeRepository.findByNameContainingIgnoreCase(name);
        }

        // Category only
        if (hasCategory) {
            return cakeRepository.findByCategoryIgnoreCase(category);
        }

        // Price range only
        if (hasMinPrice && hasMaxPrice) {
            return cakeRepository.findByPriceBetween(minPrice, maxPrice);
        }

        return cakeRepository.findAll();
    }

    // Get cake by ID
    public Cake getCakeById(Long id) {
        return cakeRepository.findById(id).orElse(null);
    }

    // Add a new cake
    public Cake addCake(Cake cake) {
        return cakeRepository.save(cake);
    }

    // Update an existing cake
    public Cake updateCake(Long id, Cake cake) {

        Cake existingCake = cakeRepository.findById(id).orElse(null);

        if (existingCake == null) {
            return null;
        }

        existingCake.setName(cake.getName());
        existingCake.setDescription(cake.getDescription());
        existingCake.setCategory(cake.getCategory());
        existingCake.setPrice(cake.getPrice());
        existingCake.setAvailable(cake.isAvailable());
        existingCake.setImageUrl(cake.getImageUrl());

        return cakeRepository.save(existingCake);
    }

    // Delete a cake
    public boolean deleteCake(Long id) {

        if (!cakeRepository.existsById(id)) {
            return false;
        }

        cakeRepository.deleteById(id);
        return true;
    }
}