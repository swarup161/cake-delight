package com.cakedelight.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cakedelight.order.client.CatalogClient;
import com.cakedelight.order.event.OrderCompletedEvent;
import com.cakedelight.order.messaging.OrderEventPublisher;
import com.cakedelight.order.model.Order;
import com.cakedelight.order.model.OrderItem;
import com.cakedelight.order.model.OrderStatus;
import com.cakedelight.order.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final CatalogClient catalogClient;

    private final OrderEventPublisher orderEventPublisher;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OrderService(
            OrderRepository orderRepository,
            CatalogClient catalogClient,
            OrderEventPublisher orderEventPublisher) {

        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
        this.orderEventPublisher = orderEventPublisher;
    }


    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }


    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    public Order getOrderById(Long id) {

        return orderRepository
                .findById(id)
                .orElse(null);
    }


    // =========================================================
    // CREATE ORDER / CHECKOUT
    // =========================================================

    public Order createOrder(Order order) {

        // -----------------------------------------------------
        // 1. INITIAL ORDER DETAILS
        // -----------------------------------------------------

        order.setStatus(OrderStatus.CREATED);

        order.setOrderDate(
                LocalDateTime.now()
        );


        // -----------------------------------------------------
        // 2. SET ORDER REFERENCE FOR EACH ITEM
        // -----------------------------------------------------

        if (order.getItems() == null) {

            order.setItems(
                    new ArrayList<>()
            );
        }


        for (OrderItem item : order.getItems()) {

            item.setOrder(order);
        }


        // -----------------------------------------------------
        // 3. CALCULATE TOTAL FROM ITEMS
        // -----------------------------------------------------

        order.setTotalAmount(
                order.calculateTotal()
        );


        // -----------------------------------------------------
        // 4. SAVE ORDER
        // -----------------------------------------------------

        Order savedOrder =
                orderRepository.save(order);


        System.out.println();
        System.out.println("======================================");
        System.out.println("ORDER CREATED");
        System.out.println("======================================");

        System.out.println(
                "Order ID      : "
                        + savedOrder.getId()
        );

        System.out.println(
                "Customer      : "
                        + savedOrder.getCustomerName()
        );

        System.out.println(
                "Email         : "
                        + savedOrder.getCustomerEmail()
        );

        System.out.println(
                "Total         : ₹"
                        + savedOrder.getTotalAmount()
        );


        // -----------------------------------------------------
        // 5. PRINT ORDER ITEMS
        // -----------------------------------------------------

        System.out.println();
        System.out.println("ORDER ITEMS:");
        System.out.println("--------------------------------------");

        if (savedOrder.getItems() != null
                && !savedOrder.getItems().isEmpty()) {

            for (OrderItem item :
                    savedOrder.getItems()) {

                System.out.println(
                        "Cake ID : "
                                + item.getCakeId()
                );

                System.out.println(
                        "Cake    : "
                                + item.getCakeName()
                );

                System.out.println(
                        "Qty     : "
                                + item.getQuantity()
                );

                System.out.println(
                        "Price   : ₹"
                                + item.getPrice()
                );

                System.out.println(
                        "Subtotal: ₹"
                                + item.getSubtotal()
                );

                System.out.println(
                        "--------------------------------------"
                );
            }

        } else {

            System.out.println(
                    "WARNING: NO ORDER ITEMS FOUND!"
            );
        }


        // -----------------------------------------------------
        // 6. MARK ORDER COMPLETED
        // -----------------------------------------------------

        savedOrder.setStatus(
                OrderStatus.COMPLETED
        );

        savedOrder =
                orderRepository.save(savedOrder);


        // =====================================================
        // 7. CREATE EVENT ITEMS
        // =====================================================

        List<OrderCompletedEvent.OrderItemEvent>
                eventItems =
                new ArrayList<>();


        if (savedOrder.getItems() != null) {

            for (OrderItem item :
                    savedOrder.getItems()) {

                OrderCompletedEvent.OrderItemEvent
                        eventItem =
                        new OrderCompletedEvent.OrderItemEvent(

                                item.getCakeId(),

                                item.getCakeName(),

                                item.getPrice(),

                                item.getQuantity()
                        );

                eventItems.add(eventItem);
            }
        }


        // =====================================================
        // 8. CREATE COMPLETION EVENT
        // =====================================================

        OrderCompletedEvent event =
                new OrderCompletedEvent(

                        savedOrder.getId(),

                        savedOrder.getCustomerName(),

                        savedOrder.getCustomerPhone(),

                        savedOrder.getCustomerEmail(),

                        savedOrder.getDeliveryAddress(),

                        savedOrder.getTotalAmount(),

                        savedOrder.getOrderDate(),

                        eventItems
                );


        // =====================================================
        // 9. DEBUG EVENT
        // =====================================================

        System.out.println();
        System.out.println("======================================");
        System.out.println("ORDER COMPLETED EVENT");
        System.out.println("======================================");

        System.out.println(
                "Order ID : "
                        + event.getOrderId()
        );

        System.out.println(
                "Customer : "
                        + event.getCustomerName()
        );

        System.out.println(
                "Email    : "
                        + event.getCustomerEmail()
        );

        System.out.println(
                "Total    : ₹"
                        + event.getTotalAmount()
        );

        System.out.println();
        System.out.println("EVENT ITEMS:");
        System.out.println("--------------------------------------");


        if (event.getItems() != null
                && !event.getItems().isEmpty()) {

            for (
                    OrderCompletedEvent.OrderItemEvent item
                    : event.getItems()
            ) {

                System.out.println(
                        "Cake ID : "
                                + item.getCakeId()
                );

                System.out.println(
                        "Cake    : "
                                + item.getCakeName()
                );

                System.out.println(
                        "Qty     : "
                                + item.getQuantity()
                );

                System.out.println(
                        "Price   : ₹"
                                + item.getPrice()
                );

                System.out.println(
                        "--------------------------------------"
                );
            }

        } else {

            System.out.println(
                    "WARNING: EVENT CONTAINS NO ITEMS!"
            );
        }


        // =====================================================
        // 10. PUBLISH EVENT TO RABBITMQ
        // =====================================================

        orderEventPublisher
                .publishOrderCompleted(event);


        System.out.println();
        System.out.println(
                "✅ ORDER COMPLETED EVENT PUBLISHED"
        );

        System.out.println(
                "======================================"
        );


        // =====================================================
        // 11. RETURN ORDER
        // =====================================================

        return savedOrder;
    }


    // =========================================================
    // ADD ITEM TO BASKET
    // =========================================================

    public Order addItemToBasket(
            Long orderId,
            OrderItem item) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElse(null);

        if (order == null) {
            return null;
        }


        // Check whether cake already exists

        for (OrderItem existingItem :
                order.getItems()) {

            if (
                    existingItem
                            .getCakeId()
                            .equals(item.getCakeId())
            ) {

                int newQuantity =
                        existingItem.getQuantity()
                                + item.getQuantity();

                existingItem.setQuantity(
                        newQuantity
                );

                order.setTotalAmount(
                        order.calculateTotal()
                );

                return orderRepository.save(order);
            }
        }


        // Add new cake

        item.setOrder(order);

        order.getItems().add(item);

        order.setTotalAmount(
                order.calculateTotal()
        );

        return orderRepository.save(order);
    }


    // =========================================================
    // GET BASKET
    // =========================================================

    public List<OrderItem> getBasket(
            Long orderId) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElse(null);

        if (order == null) {
            return null;
        }

        return order.getItems();
    }


    // =========================================================
    // UPDATE BASKET ITEM QUANTITY
    // =========================================================

    public Order updateBasketItem(
            Long orderId,
            Long itemId,
            Integer quantity) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElse(null);

        if (order == null) {
            return null;
        }


        for (OrderItem item :
                order.getItems()) {

            if (
                    item.getId()
                            .equals(itemId)
            ) {

                if (
                        quantity == null
                                || quantity < 1
                ) {

                    return null;
                }

                item.setQuantity(quantity);

                order.setTotalAmount(
                        order.calculateTotal()
                );

                return orderRepository.save(order);
            }
        }

        return null;
    }


    // =========================================================
    // REMOVE BASKET ITEM
    // =========================================================

    public Order removeBasketItem(
            Long orderId,
            Long itemId) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElse(null);

        if (order == null) {
            return null;
        }


        OrderItem itemToRemove = null;


        for (OrderItem item :
                order.getItems()) {

            if (
                    item.getId()
                            .equals(itemId)
            ) {

                itemToRemove = item;

                break;
            }
        }


        if (itemToRemove == null) {
            return null;
        }


        order.removeItem(itemToRemove);


        order.setTotalAmount(
                order.calculateTotal()
        );


        return orderRepository.save(order);
    }


    // =========================================================
    // CALCULATE ORDER TOTAL
    // =========================================================

    public BigDecimal calculateOrderTotal(
            Long orderId) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElse(null);

        if (order == null) {
            return null;
        }


        BigDecimal total =
                order.calculateTotal();


        order.setTotalAmount(total);


        orderRepository.save(order);


        return total;
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    public Order updateOrderStatus(
            Long id,
            OrderStatus status) {

        Order existingOrder =
                orderRepository
                        .findById(id)
                        .orElse(null);

        if (existingOrder == null) {
            return null;
        }


        existingOrder.setStatus(status);


        return orderRepository.save(
                existingOrder
        );
    }


    // =========================================================
    // DELETE ORDER
    // =========================================================

    public boolean deleteOrder(Long id) {

        if (!orderRepository.existsById(id)) {
            return false;
        }


        orderRepository.deleteById(id);


        return true;
    }


    // =========================================================
    // GET CAKE DETAILS FROM CATALOG
    // =========================================================

    public String getCakeDetails(Long cakeId) {

        return catalogClient.getCake(cakeId);
    }
}