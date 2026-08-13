package com.cakedelight.order.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE =
            "cake.order.exchange";

    public static final String ORDER_QUEUE =
            "cake.order.completed.queue";

    public static final String ORDER_ROUTING_KEY =
            "order.completed";

    // RabbitMQ Admin
    @Bean
    public RabbitAdmin rabbitAdmin(
            ConnectionFactory connectionFactory) {

        RabbitAdmin rabbitAdmin =
                new RabbitAdmin(connectionFactory);

        rabbitAdmin.setAutoStartup(true);

        return rabbitAdmin;
    }

    // Exchange
    @Bean
    public DirectExchange orderExchange() {

        return new DirectExchange(
                ORDER_EXCHANGE,
                true,
                false
        );
    }

    // Queue
    @Bean
    public Queue orderQueue() {

        return QueueBuilder
                .durable(ORDER_QUEUE)
                .build();
    }

    // Binding
    @Bean
    public Binding orderBinding(
            Queue orderQueue,
            DirectExchange orderExchange) {

        return BindingBuilder
                .bind(orderQueue)
                .to(orderExchange)
                .with(ORDER_ROUTING_KEY);
    }

    // Force RabbitMQ declaration when application starts
    @Bean
    public ApplicationRunner rabbitMqInitializer(
            RabbitAdmin rabbitAdmin,
            DirectExchange orderExchange,
            Queue orderQueue,
            Binding orderBinding) {

        return args -> {

            rabbitAdmin.declareExchange(orderExchange);
            rabbitAdmin.declareQueue(orderQueue);
            rabbitAdmin.declareBinding(orderBinding);

            System.out.println(
                    "========================================"
            );
            System.out.println(
                    "RabbitMQ exchange/queue/binding created!"
            );
            System.out.println(
                    "Exchange : " + ORDER_EXCHANGE
            );
            System.out.println(
                    "Queue    : " + ORDER_QUEUE
            );
            System.out.println(
                    "Routing  : " + ORDER_ROUTING_KEY
            );
            System.out.println(
                    "========================================"
            );
        };
    }

    // JSON converter
    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {

        return new JacksonJsonMessageConverter();
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter) {

        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }
}