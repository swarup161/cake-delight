package com.cakedelight.catalog.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cakedelight.catalog.model.Cake;
import com.cakedelight.catalog.service.CakeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cakes")
public class CakeController {

    private final CakeService cakeService;

    public CakeController(CakeService cakeService) {
        this.cakeService = cakeService;
    }

    // ==========================================
    // GET ALL CAKES + FILTERING
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Cake>> getAllCakes(

            @RequestParam(required = false) String name,

            @RequestParam(required = false) String category,

            @RequestParam(required = false) BigDecimal minPrice,

            @RequestParam(required = false) BigDecimal maxPrice) {

        return ResponseEntity.ok(
                cakeService.getAllCakes(
                        name,
                        category,
                        minPrice,
                        maxPrice
                )
        );
    }

    // ==========================================
    // GET CAKE BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<Cake> getCakeById(
            @PathVariable Long id) {

        Cake cake = cakeService.getCakeById(id);

        if (cake == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cake);
    }

    // ==========================================
    // POST - ADD NEW CAKE
    // ==========================================

    @PostMapping
    public ResponseEntity<Cake> addCake(
            @Valid @RequestBody Cake cake) {

        Cake savedCake = cakeService.addCake(cake);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedCake);
    }

    // ==========================================
    // PUT - UPDATE CAKE
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<Cake> updateCake(
            @PathVariable Long id,
            @Valid @RequestBody Cake cake) {

        Cake updatedCake = cakeService.updateCake(id, cake);

        if (updatedCake == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedCake);
    }

    // ==========================================
    // DELETE - DELETE CAKE
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCake(
            @PathVariable Long id) {

        boolean deleted = cakeService.deleteCake(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}