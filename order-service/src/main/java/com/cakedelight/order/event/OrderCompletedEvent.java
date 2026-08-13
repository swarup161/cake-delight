package com.cakedelight.order.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderCompletedEvent {

    // =========================================================
    // ORDER DETAILS
    // =========================================================

    private Long orderId;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String deliveryAddress;

    private BigDecimal totalAmount;

    private LocalDateTime orderDate;


    // =========================================================
    // ORDER ITEMS
    // =========================================================

    private List<OrderItemEvent> items = new ArrayList<>();


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public OrderCompletedEvent() {
    }


    // =========================================================
    // PARAMETERIZED CONSTRUCTOR
    // =========================================================

    public OrderCompletedEvent(
            Long orderId,
            String customerName,
            String customerPhone,
            String customerEmail,
            String deliveryAddress,
            BigDecimal totalAmount,
            LocalDateTime orderDate,
            List<OrderItemEvent> items) {

        this.orderId = orderId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;

        if (items != null) {
            this.items = new ArrayList<>(items);
        } else {
            this.items = new ArrayList<>();
        }
    }


    // =========================================================
    // ORDER ID
    // =========================================================

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
    // ORDER DATE
    // =========================================================

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }


    // =========================================================
    // ORDER ITEMS
    // =========================================================

    public List<OrderItemEvent> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEvent> items) {

        if (items != null) {
            this.items = new ArrayList<>(items);
        } else {
            this.items = new ArrayList<>();
        }
    }


    // =========================================================
    // INNER CLASS - ORDER ITEM EVENT
    // =========================================================

    public static class OrderItemEvent {

        private Long cakeId;

        private String cakeName;

        private BigDecimal price;

        private Integer quantity;


        // =====================================================
        // DEFAULT CONSTRUCTOR
        // =====================================================

        public OrderItemEvent() {
        }


        // =====================================================
        // PARAMETERIZED CONSTRUCTOR
        // =====================================================

        public OrderItemEvent(
                Long cakeId,
                String cakeName,
                BigDecimal price,
                Integer quantity) {

            this.cakeId = cakeId;
            this.cakeName = cakeName;
            this.price = price;
            this.quantity = quantity;
        }


        // =====================================================
        // CAKE ID
        // =====================================================

        public Long getCakeId() {
            return cakeId;
        }

        public void setCakeId(Long cakeId) {
            this.cakeId = cakeId;
        }


        // =====================================================
        // CAKE NAME
        // =====================================================

        public String getCakeName() {
            return cakeName;
        }

        public void setCakeName(String cakeName) {
            this.cakeName = cakeName;
        }


        // =====================================================
        // PRICE
        // =====================================================

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }


        // =====================================================
        // QUANTITY
        // =====================================================

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}