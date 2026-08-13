package com.cakedelight.notification_service.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cakedelight.notification_service.event.OrderCompletedEvent;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    // =========================================================
    // SEND ORDER CONFIRMATION EMAIL
    // =========================================================

    public void sendOrderConfirmation(
            String email,
            String customerName,
            Long orderId,
            String deliveryAddress,
            BigDecimal totalAmount,
            List<OrderCompletedEvent.OrderItemEvent> items) {


        try {

            // =================================================
            // CREATE HTML EMAIL
            // =================================================

            MimeMessage message =
                    mailSender.createMimeMessage();


            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            StandardCharsets.UTF_8.name()
                    );


            // =================================================
            // SENDER
            // =================================================

            helper.setFrom(
                    "cakedelight108@gmail.com"
            );


            // =================================================
            // CUSTOMER EMAIL
            // =================================================

            helper.setTo(email);


            // =================================================
            // SUBJECT
            // =================================================

            helper.setSubject(
                    "🎂 Cake Delight - Order #"
                            + orderId
                            + " Confirmed"
            );


            // =================================================
            // BUILD ORDER ITEMS HTML
            // =================================================

            StringBuilder itemsHtml =
                    new StringBuilder();


            if (
                    items != null
                            && !items.isEmpty()
            ) {

                for (
                        OrderCompletedEvent.OrderItemEvent item
                        : items
                ) {

                    BigDecimal price =
                            item.getPrice() != null
                                    ? item.getPrice()
                                    : BigDecimal.ZERO;


                    int quantity =
                            item.getQuantity() != null
                                    ? item.getQuantity()
                                    : 0;


                    BigDecimal subtotal =
                            price.multiply(
                                    BigDecimal.valueOf(quantity)
                            );


                    itemsHtml.append(

                            "<tr>"

                                    // Cake name
                                    + "<td style=\""
                                    + "padding:14px 10px;"
                                    + "border-bottom:1px solid #f0dfe5;"
                                    + "font-size:14px;"
                                    + "color:#333;"
                                    + "\">"

                                    + "<strong>"
                                    + escapeHtml(
                                            item.getCakeName()
                                    )
                                    + "</strong>"

                                    + "</td>"


                                    // Quantity
                                    + "<td style=\""
                                    + "padding:14px 10px;"
                                    + "border-bottom:1px solid #f0dfe5;"
                                    + "text-align:center;"
                                    + "font-size:14px;"
                                    + "color:#333;"
                                    + "\">"

                                    + quantity

                                    + "</td>"


                                    // Price
                                    + "<td style=\""
                                    + "padding:14px 10px;"
                                    + "border-bottom:1px solid #f0dfe5;"
                                    + "text-align:right;"
                                    + "font-size:14px;"
                                    + "color:#333;"
                                    + "\">"

                                    + "₹"
                                    + price

                                    + "</td>"


                                    // Subtotal
                                    + "<td style=\""
                                    + "padding:14px 10px;"
                                    + "border-bottom:1px solid #f0dfe5;"
                                    + "text-align:right;"
                                    + "font-size:14px;"
                                    + "font-weight:bold;"
                                    + "color:#333;"
                                    + "\">"

                                    + "₹"
                                    + subtotal

                                    + "</td>"

                                    + "</tr>"
                    );
                }

            } else {

                itemsHtml.append(

                        "<tr>"
                                + "<td colspan=\"4\" "
                                + "style=\""
                                + "padding:20px;"
                                + "text-align:center;"
                                + "color:#777;"
                                + "\">"
                                + "No order items available."
                                + "</td>"
                                + "</tr>"
                );
            }


            // =================================================
            // HTML EMAIL
            // =================================================

            String html =

                    "<!DOCTYPE html>"

                    + "<html>"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "</head>"

                    + "<body style=\""
                    + "margin:0;"
                    + "padding:0;"
                    + "background:#fff7fa;"
                    + "font-family:Arial,Helvetica,sans-serif;"
                    + "\">"


                    // =================================================
                    // MAIN CONTAINER
                    // =================================================

                    + "<div style=\""
                    + "max-width:650px;"
                    + "margin:30px auto;"
                    + "background:#ffffff;"
                    + "border-radius:16px;"
                    + "overflow:hidden;"
                    + "box-shadow:0 4px 18px rgba(0,0,0,0.08);"
                    + "\">"


                    // =================================================
                    // CAKE DELIGHT BANNER
                    // =================================================

                    + "<div style=\""
                    + "background:linear-gradient(135deg,#ff5c8a,#e83e72);"
                    + "padding:28px 20px;"
                    + "text-align:center;"
                    + "color:white;"
                    + "\">"

                    + "<div style=\""
                    + "font-size:42px;"
                    + "margin-bottom:8px;"
                    + "\">"
                    + "🎂"
                    + "</div>"

                    + "<div style=\""
                    + "font-size:30px;"
                    + "font-weight:bold;"
                    + "letter-spacing:1px;"
                    + "\">"
                    + "Cake <span style=\"color:#ffe5ed;\">Delight</span>"
                    + "</div>"

                    + "<div style=\""
                    + "font-size:13px;"
                    + "margin-top:8px;"
                    + "opacity:0.95;"
                    + "\">"
                    + "Fresh cakes for every celebration ❤️"
                    + "</div>"

                    + "</div>"


                    // =================================================
                    // CONTENT
                    // =================================================

                    + "<div style=\"padding:30px;\">"


                    // Greeting
                    + "<h2 style=\""
                    + "margin:0 0 10px 0;"
                    + "color:#333;"
                    + "\">"

                    + "Hello "
                    + escapeHtml(customerName)
                    + " 👋"

                    + "</h2>"


                    + "<p style=\""
                    + "font-size:15px;"
                    + "line-height:1.7;"
                    + "color:#555;"
                    + "\">"

                    + "Thank you for ordering from "
                    + "<strong>Cake Delight</strong>! ❤️"

                    + "</p>"


                    + "<p style=\""
                    + "font-size:15px;"
                    + "line-height:1.7;"
                    + "color:#555;"
                    + "\">"

                    + "Your order has been "
                    + "<strong style=\"color:#e83e72;\">"
                    + "confirmed successfully"
                    + "</strong>."

                    + "</p>"


                    // =================================================
                    // ORDER ID BOX
                    // =================================================

                    + "<div style=\""
                    + "background:#fff0f5;"
                    + "border-left:5px solid #e83e72;"
                    + "padding:15px 18px;"
                    + "margin:25px 0;"
                    + "border-radius:8px;"
                    + "\">"

                    + "<div style=\""
                    + "font-size:13px;"
                    + "color:#888;"
                    + "\">"
                    + "ORDER NUMBER"
                    + "</div>"

                    + "<div style=\""
                    + "font-size:24px;"
                    + "font-weight:bold;"
                    + "color:#e83e72;"
                    + "margin-top:4px;"
                    + "\">"

                    + "#"
                    + orderId

                    + "</div>"

                    + "</div>"


                    // =================================================
                    // ORDER ITEMS HEADING
                    // =================================================

                    + "<h3 style=\""
                    + "color:#333;"
                    + "margin:25px 0 12px 0;"
                    + "\">"

                    + "🍰 Order Items"

                    + "</h3>"


                    // =================================================
                    // ORDER ITEMS TABLE
                    // =================================================

                    + "<table width=\"100%\" "
                    + "cellpadding=\"0\" "
                    + "cellspacing=\"0\" "
                    + "style=\""
                    + "border-collapse:collapse;"
                    + "border:1px solid #f0dfe5;"
                    + "border-radius:8px;"
                    + "overflow:hidden;"
                    + "\">"


                    // Table header
                    + "<thead>"

                    + "<tr style=\""
                    + "background:#fff0f5;"
                    + "\">"

                    + "<th style=\""
                    + "padding:13px 10px;"
                    + "text-align:left;"
                    + "font-size:13px;"
                    + "color:#555;"
                    + "\">"
                    + "Cake"
                    + "</th>"

                    + "<th style=\""
                    + "padding:13px 10px;"
                    + "text-align:center;"
                    + "font-size:13px;"
                    + "color:#555;"
                    + "\">"
                    + "Qty"
                    + "</th>"

                    + "<th style=\""
                    + "padding:13px 10px;"
                    + "text-align:right;"
                    + "font-size:13px;"
                    + "color:#555;"
                    + "\">"
                    + "Price"
                    + "</th>"

                    + "<th style=\""
                    + "padding:13px 10px;"
                    + "text-align:right;"
                    + "font-size:13px;"
                    + "color:#555;"
                    + "\">"
                    + "Subtotal"
                    + "</th>"

                    + "</tr>"

                    + "</thead>"


                    // Table body
                    + "<tbody>"

                    + itemsHtml.toString()

                    + "</tbody>"

                    + "</table>"


                    // =================================================
                    // TOTAL
                    // =================================================

                    + "<div style=\""
                    + "margin-top:20px;"
                    + "padding:18px;"
                    + "background:#fff8fa;"
                    + "border-radius:10px;"
                    + "text-align:right;"
                    + "\">"

                    + "<span style=\""
                    + "font-size:16px;"
                    + "color:#555;"
                    + "\">"
                    + "Total Amount"
                    + "</span>"

                    + "<br>"

                    + "<span style=\""
                    + "font-size:26px;"
                    + "font-weight:bold;"
                    + "color:#e83e72;"
                    + "\">"

                    + "₹"
                    + totalAmount

                    + "</span>"

                    + "</div>"


                    // =================================================
                    // DELIVERY INFORMATION
                    // =================================================

                    + "<h3 style=\""
                    + "color:#333;"
                    + "margin:28px 0 12px 0;"
                    + "\">"

                    + "📦 Delivery Details"

                    + "</h3>"


                    + "<div style=\""
                    + "background:#fafafa;"
                    + "border:1px solid #eeeeee;"
                    + "padding:18px;"
                    + "border-radius:10px;"
                    + "\">"

                    + "<p style=\""
                    + "margin:0 0 10px 0;"
                    + "font-size:14px;"
                    + "color:#555;"
                    + "\">"

                    + "<strong>Address:</strong><br>"
                    + escapeHtml(deliveryAddress)

                    + "</p>"


                    + "<p style=\""
                    + "margin:0;"
                    + "font-size:14px;"
                    + "color:#555;"
                    + "\">"

                    + "<strong>Status:</strong> "

                    + "<span style=\""
                    + "color:#198754;"
                    + "font-weight:bold;"
                    + "\">"
                    + "CONFIRMED"
                    + "</span>"

                    + "</p>"

                    + "</div>"


                    // =================================================
                    // THANK YOU MESSAGE
                    // =================================================

                    + "<div style=\""
                    + "text-align:center;"
                    + "margin-top:30px;"
                    + "padding:20px 10px;"
                    + "border-top:1px solid #eeeeee;"
                    + "\">"

                    + "<div style=\""
                    + "font-size:24px;"
                    + "margin-bottom:8px;"
                    + "\">"
                    + "🎂 ❤️"
                    + "</div>"

                    + "<p style=\""
                    + "margin:0;"
                    + "font-size:15px;"
                    + "color:#555;"
                    + "\">"

                    + "We will deliver your delicious cake soon!"

                    + "</p>"

                    + "<p style=\""
                    + "margin:10px 0 0 0;"
                    + "font-size:14px;"
                    + "color:#777;"
                    + "\">"

                    + "Thank you for choosing "
                    + "<strong>Cake Delight</strong>."

                    + "</p>"

                    + "</div>"


                    // =================================================
                    // FOOTER
                    // =================================================

                    + "<div style=\""
                    + "background:#292326;"
                    + "padding:20px;"
                    + "text-align:center;"
                    + "color:#ffffff;"
                    + "\">"

                    + "<div style=\""
                    + "font-size:18px;"
                    + "font-weight:bold;"
                    + "\">"
                    + "🎂 Cake Delight"
                    + "</div>"

                    + "<div style=\""
                    + "font-size:12px;"
                    + "margin-top:6px;"
                    + "color:#dddddd;"
                    + "\">"
                    + "Fresh cakes for every celebration"
                    + "</div>"

                    + "<div style=\""
                    + "font-size:11px;"
                    + "margin-top:12px;"
                    + "color:#aaaaaa;"
                    + "\">"
                    + "© Cake Delight Team"
                    + "</div>"

                    + "</div>"


                    // End main container
                    + "</div>"

                    + "</body>"
                    + "</html>";


            // =================================================
            // SET HTML CONTENT
            // =================================================

            helper.setText(
                    html,
                    true
            );


            // =================================================
            // SEND EMAIL
            // =================================================

            mailSender.send(message);


            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "EMAIL SENT SUCCESSFULLY"
            );

            System.out.println(
                    "To : " + email
            );

            System.out.println(
                    "Order ID : #" + orderId
            );

            System.out.println(
                    "======================================"
            );


        } catch (Exception e) {

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "EMAIL SENDING FAILED"
            );

            System.out.println(
                    "To : " + email
            );

            System.out.println(
                    "Order ID : #" + orderId
            );

            System.out.println(
                    "Error : " + e.getMessage()
            );

            System.out.println(
                    "======================================"
            );

            throw new RuntimeException(
                    "Failed to send order confirmation email",
                    e
            );
        }
    }


    // =========================================================
    // SIMPLE HTML ESCAPE
    // =========================================================

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}