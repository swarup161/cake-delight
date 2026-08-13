package com.cakedelight.notification_service.service;

import com.cakedelight.notification_service.model.Notification;
import com.cakedelight.notification_service.repository.NotificationRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Create a new notification
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    // Update an existing notification
    public Notification updateNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    // Get all notifications
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    // Get notifications for a specific order
    public List<Notification> getNotificationsByOrder(Long orderId) {
        return notificationRepository.findByOrderId(orderId);
    }
}