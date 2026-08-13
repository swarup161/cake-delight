package com.cakedelight.order.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.cakedelight.order.config.RabbitMQConfig;
import com.cakedelight.order.event.OrderCompletedEvent;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCompleted(OrderCompletedEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                event
        );

        System.out.println(
                "Order completed event published for order ID: "
                        + event.getOrderId()
        );
    }
}