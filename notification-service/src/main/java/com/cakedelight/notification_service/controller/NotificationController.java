package com.cakedelight.notification_service.controller;

import com.cakedelight.notification_service.model.Notification;
import com.cakedelight.notification_service.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/order/{orderId}")
    public List<Notification> getNotificationsByOrder(
            @PathVariable Long orderId) {
        return notificationService.getNotificationsByOrder(orderId);
    }
}