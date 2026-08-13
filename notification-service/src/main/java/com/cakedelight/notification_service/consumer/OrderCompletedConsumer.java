package com.cakedelight.notification_service.consumer;

import com.cakedelight.notification_service.event.OrderCompletedEvent;
import com.cakedelight.notification_service.model.Notification;
import com.cakedelight.notification_service.service.EmailService;
import com.cakedelight.notification_service.service.NotificationService;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCompletedConsumer {

    // =========================================================
    // SERVICES
    // =========================================================

    private final NotificationService notificationService;
    private final EmailService emailService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OrderCompletedConsumer(
            NotificationService notificationService,
            EmailService emailService) {

        this.notificationService = notificationService;
        this.emailService = emailService;
    }


    // =========================================================
    // RABBITMQ LISTENER
    // =========================================================

    @RabbitListener(
            queues = "cake.order.completed.queue"
    )
    public void handleOrderCompleted(
            OrderCompletedEvent event) {

        System.out.println();
        System.out.println("======================================");
        System.out.println("ORDER COMPLETED EVENT RECEIVED");
        System.out.println("======================================");


        // =====================================================
        // 1. DISPLAY ORDER DETAILS
        // =====================================================

        System.out.println(
                "Order ID         : "
                        + event.getOrderId()
        );

        System.out.println(
                "Customer Name    : "
                        + event.getCustomerName()
        );

        System.out.println(
                "Customer Phone   : "
                        + event.getCustomerPhone()
        );

        System.out.println(
                "Customer Email   : "
                        + event.getCustomerEmail()
        );

        System.out.println(
                "Delivery Address : "
                        + event.getDeliveryAddress()
        );

        System.out.println(
                "Total Amount     : ₹"
                        + event.getTotalAmount()
        );

        System.out.println(
                "Order Date       : "
                        + event.getOrderDate()
        );


        // =====================================================
        // 2. DISPLAY ORDER ITEMS
        // =====================================================

        System.out.println();
        System.out.println("ORDER ITEMS");
        System.out.println("--------------------------------------");


        if (event.getItems() != null
                && !event.getItems().isEmpty()) {

            for (
                    OrderCompletedEvent.OrderItemEvent item
                    : event.getItems()
            ) {

                System.out.println(
                        "Cake Name : "
                                + item.getCakeName()
                );

                System.out.println(
                        "Quantity  : "
                                + item.getQuantity()
                );

                System.out.println(
                        "Price     : ₹"
                                + item.getPrice()
                );

                System.out.println("--------------------------------------");
            }

        } else {

            System.out.println(
                    "No order items found."
            );

            System.out.println("--------------------------------------");
        }


        // =====================================================
        // 3. CREATE NOTIFICATION
        // =====================================================

        Notification notification =
                new Notification();

        notification.setOrderId(
                event.getOrderId()
        );

        notification.setCustomerEmail(
                event.getCustomerEmail()
        );

        notification.setCustomerName(
                event.getCustomerName()
        );

        notification.setCustomerPhone(
                event.getCustomerPhone()
        );

        notification.setMessage(
                "Your Cake Delight order #"
                        + event.getOrderId()
                        + " has been confirmed!"
        );

        notification.setStatus(
                "PENDING"
        );


        Notification saved =
                notificationService
                        .createNotification(
                                notification
                        );


        System.out.println();
        System.out.println(
                "Notification created."
        );

        System.out.println(
                "Notification ID : "
                        + saved.getId()
        );

        System.out.println(
                "Status          : PENDING"
        );


        // =====================================================
        // 4. SEND ORDER CONFIRMATION EMAIL
        // =====================================================

        try {

            emailService.sendOrderConfirmation(

                    event.getCustomerEmail(),

                    event.getCustomerName(),

                    event.getOrderId(),

                    event.getDeliveryAddress(),

                    event.getTotalAmount(),

                    event.getItems()
            );


            // =================================================
            // 5. EMAIL SUCCESS
            // =================================================

            saved.setStatus(
                    "SENT"
            );

            notificationService
                    .createNotification(
                            saved
                    );


            System.out.println();
            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "ORDER CONFIRMATION EMAIL SENT"
            );

            System.out.println(
                    "To : "
                            + event.getCustomerEmail()
            );

            System.out.println(
                    "Status : SENT"
            );

            System.out.println(
                    "======================================"
            );


        } catch (Exception e) {

            // ================================================
            // 6. EMAIL FAILED
            // ================================================

            saved.setStatus(
                    "FAILED"
            );

            notificationService
                    .createNotification(
                            saved
                    );


            System.out.println();
            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "ORDER CONFIRMATION EMAIL FAILED"
            );

            System.out.println(
                    "To : "
                            + event.getCustomerEmail()
            );

            System.out.println(
                    "Status : FAILED"
            );

            System.out.println(
                    "Error : "
                            + e.getMessage()
            );

            System.out.println(
                    "======================================"
            );

        }
    }
}