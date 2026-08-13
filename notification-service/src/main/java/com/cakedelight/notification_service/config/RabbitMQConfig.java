package com.cakedelight.notification_service.config;

import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {

        JacksonJsonMessageConverter converter =
                new JacksonJsonMessageConverter();

        DefaultJacksonJavaTypeMapper typeMapper =
                new DefaultJacksonJavaTypeMapper();

        typeMapper.addTrustedPackages(
                "com.cakedelight.order.event",
                "com.cakedelight.notification_service.event"
        );

        typeMapper.setTypePrecedence(
                DefaultJacksonJavaTypeMapper.TypePrecedence.INFERRED
        );

        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }
}