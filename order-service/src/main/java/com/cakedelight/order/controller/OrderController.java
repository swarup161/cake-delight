package com.cakedelight.order.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cakedelight.order.model.Order;
import com.cakedelight.order.model.OrderItem;
import com.cakedelight.order.model.OrderStatus;
import com.cakedelight.order.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long id) {

        Order order = orderService.getOrderById(id);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order);
    }

    // =========================================================
    // CREATE ORDER / CHECKOUT
    // =========================================================

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody Order order) {

        Order createdOrder =
                orderService.createOrder(order);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdOrder);
    }

    // =========================================================
    // GET CAKE DETAILS FROM CATALOG SERVICE
    // =========================================================

    @GetMapping("/cake/{cakeId}")
    public ResponseEntity<String> getCakeDetails(
            @PathVariable Long cakeId) {

        return ResponseEntity.ok(
                orderService.getCakeDetails(cakeId)
        );
    }

    // =========================================================
    // ADD ITEM TO BASKET
    // =========================================================

    @PostMapping("/{orderId}/basket")
    public ResponseEntity<Order> addItemToBasket(
            @PathVariable Long orderId,
            @RequestBody OrderItem item) {

        Order updatedOrder =
                orderService.addItemToBasket(
                        orderId,
                        item
                );

        if (updatedOrder == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedOrder);
    }

    // =========================================================
    // GET BASKET CONTENTS
    // =========================================================

    @GetMapping("/{orderId}/basket")
    public ResponseEntity<List<OrderItem>> getBasket(
            @PathVariable Long orderId) {

        List<OrderItem> items =
                orderService.getBasket(orderId);

        if (items == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(items);
    }

    // =========================================================
    // UPDATE BASKET ITEM QUANTITY
    // =========================================================

    @PutMapping("/{orderId}/basket/{itemId}")
    public ResponseEntity<Order> updateBasketItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {

        Order updatedOrder =
                orderService.updateBasketItem(
                        orderId,
                        itemId,
                        quantity
                );

        if (updatedOrder == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedOrder);
    }

    // =========================================================
    // REMOVE ITEM FROM BASKET
    // =========================================================

    @DeleteMapping("/{orderId}/basket/{itemId}")
    public ResponseEntity<Order> removeBasketItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {

        Order updatedOrder =
                orderService.removeBasketItem(
                        orderId,
                        itemId
                );

        if (updatedOrder == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedOrder);
    }

    // =========================================================
    // CALCULATE ORDER / BASKET TOTAL
    // =========================================================

    @GetMapping("/{orderId}/total")
    public ResponseEntity<BigDecimal> calculateOrderTotal(
            @PathVariable Long orderId) {

        BigDecimal total =
                orderService.calculateOrderTotal(orderId);

        if (total == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(total);
    }

    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        Order updatedOrder =
                orderService.updateOrderStatus(
                        id,
                        status
                );

        if (updatedOrder == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedOrder);
    }

    // =========================================================
    // DELETE ORDER
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Long id) {

        boolean deleted =
                orderService.deleteOrder(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}