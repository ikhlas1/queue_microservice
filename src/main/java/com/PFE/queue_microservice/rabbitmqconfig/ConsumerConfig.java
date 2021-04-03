package com.PFE.queue_microservice.rabbitmqconfig;

import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@Configuration
public class ConsumerConfig implements RabbitListenerConfigurer {

    @Bean
    public DefaultMessageHandlerMethodFactory handlerMethodFactory() {

        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(jackson2Converter());
        return factory;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {

        registrar.setMessageHandlerMethodFactory(handlerMethodFactory());
    }

    public MappingJackson2MessageConverter jackson2Converter() {

        return new MappingJackson2MessageConverter();
    }
}
