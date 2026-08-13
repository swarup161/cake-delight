package com.cakedelight.notification_service.controller;

import java.math.BigDecimal;
import java.util.Collections;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cakedelight.notification_service.service.EmailService;

@RestController
@RequestMapping("/api/test-email")
public class EmailTestController {

    private final EmailService emailService;

    public EmailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public String sendTestEmail() {

        String email = "YOUR_EMAIL@gmail.com";

        emailService.sendOrderConfirmation(
                email,
                "Swarup",
                999L,
                "Bengaluru",
                new BigDecimal("1398"),
                Collections.emptyList()
        );

        return "Test email sent successfully!";
    }
}