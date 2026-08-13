package com.cakedelight.order.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    // =========================================================
    // ORDER ID
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // CUSTOMER DETAILS
    // =========================================================

    @NotBlank
    @Column(nullable = false)
    private String customerName;


    @NotBlank
    @Column(nullable = false)
    private String customerPhone;


    @NotBlank
    @Email
    @Column(nullable = false)
    private String customerEmail;


    // =========================================================
    // DELIVERY DETAILS
    // =========================================================

    @NotBlank
    @Column(nullable = false)
    private String deliveryAddress;


    // =========================================================
    // ORDER DETAILS
    // =========================================================

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal totalAmount;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;


    @Column(nullable = false)
    private LocalDateTime orderDate;


    // =========================================================
    // ORDER ITEMS / BASKET
    // =========================================================

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public Order() {
    }


    // =========================================================
    // PARAMETERIZED CONSTRUCTOR
    // =========================================================

    public Order(
            String customerName,
            String customerPhone,
            String customerEmail,
            String deliveryAddress,
            BigDecimal totalAmount,
            OrderStatus status,
            LocalDateTime orderDate) {

        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;

        // Always initialize basket
        this.items = new ArrayList<>();
    }


    // =========================================================
    // GET ID
    // =========================================================

    public Long getId() {
        return id;
    }


    // =========================================================
    // SET ID
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }


    // =========================================================
    // CUSTOMER NAME
    // =========================================================

    public String getCustomerName() {
        return customerName;
    }


    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    // =========================================================
    // CUSTOMER PHONE
    // =========================================================

    public String getCustomerPhone() {
        return customerPhone;
    }


    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }


    // =========================================================
    // CUSTOMER EMAIL
    // =========================================================

    public String getCustomerEmail() {
        return customerEmail;
    }


    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }


    // =========================================================
    // DELIVERY ADDRESS
    // =========================================================

    public String getDeliveryAddress() {
        return deliveryAddress;
    }


    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }


    // =========================================================
    // TOTAL AMOUNT
    // =========================================================

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }


    // =========================================================
    // ORDER STATUS
    // =========================================================

    public OrderStatus getStatus() {
        return status;
    }


    public void setStatus(OrderStatus status) {
        this.status = status;
    }


    // =========================================================
    // ORDER DATE
    // =========================================================

    public LocalDateTime getOrderDate() {
        return orderDate;
    }


    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }


    // =========================================================
    // ORDER ITEMS / BASKET
    // =========================================================

    public List<OrderItem> getItems() {

        if (items == null) {
            items = new ArrayList<>();
        }

        return items;
    }


    public void setItems(List<OrderItem> items) {

        if (items == null) {
            this.items = new ArrayList<>();
        } else {
            this.items = items;
        }
    }


    // =========================================================
    // ADD ITEM TO ORDER / BASKET
    // =========================================================

    public void addItem(OrderItem item) {

        if (item == null) {
            return;
        }

        if (items == null) {
            items = new ArrayList<>();
        }

        item.setOrder(this);

        items.add(item);
    }


    // =========================================================
    // REMOVE ITEM FROM ORDER / BASKET
    // =========================================================

    public void removeItem(OrderItem item) {

        if (item == null || items == null) {
            return;
        }

        items.remove(item);

        item.setOrder(null);
    }


    // =========================================================
    // CALCULATE ORDER TOTAL
    // =========================================================

    public BigDecimal calculateTotal() {

        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .filter(item -> item != null)
                .map(OrderItem::getSubtotal)
                .filter(subtotal -> subtotal != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
}