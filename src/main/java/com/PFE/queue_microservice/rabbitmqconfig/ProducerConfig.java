package com.PFE.queue_microservice.rabbitmqconfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import java.util.Objects;


@Configuration
public class ProducerConfig{

    @Autowired
    Environment env;

    @Bean
    org.springframework.amqp.core.Queue turnQueue() {

        return new org.springframework.amqp.core.Queue(Objects.requireNonNull(env.getProperty("rabbitmq.queue.turn")), false);
    }

    @Bean
    org.springframework.amqp.core.Queue addedQueue() {

        return new org.springframework.amqp.core.Queue(Objects.requireNonNull(env.getProperty("rabbitmq.queue.added")), false);
    }

    @Bean
    org.springframework.amqp.core.Queue lateQueue() {

        return new org.springframework.amqp.core.Queue(Objects.requireNonNull(env.getProperty("rabbitmq.queue.late")), false);
    }

    @Bean
    org.springframework.amqp.core.Queue statusQueue() {

        return new org.springframework.amqp.core.Queue(Objects.requireNonNull(env.getProperty("rabbitmq.queue.status")), false);
    }

    @Bean
    org.springframework.amqp.core.Queue timeStampQueue() {

        return new org.springframework.amqp.core.Queue(Objects.requireNonNull(env.getProperty("rabbitmq.queue.timestamp")), false);
    }

    @Bean
    DirectExchange exchange() {

        return new DirectExchange(env.getProperty("rabbitmq.exchange.name"));
    }

    @Bean
    Binding turnBinding(org.springframework.amqp.core.Queue turnQueue, DirectExchange exchange) {

        return BindingBuilder.bind(turnQueue).to(exchange).with(env.getProperty("rabbitmq.routingkey.turn"));
    }

    @Bean
    Binding lateBinding(org.springframework.amqp.core.Queue lateQueue, DirectExchange exchange) {

        return BindingBuilder.bind(lateQueue).to(exchange).with(env.getProperty("rabbitmq.routingkey.late"));
    }

    @Bean
    Binding addedBinding(org.springframework.amqp.core.Queue addedQueue, DirectExchange exchange) {

        return BindingBuilder.bind(addedQueue).to(exchange).with(env.getProperty("rabbitmq.routingkey.added"));
    }

    @Bean
    Binding statusBinding(org.springframework.amqp.core.Queue statusQueue, DirectExchange exchange) {

        return BindingBuilder.bind(statusQueue).to(exchange).with(env.getProperty("rabbitmq.routingkey.status"));
    }

    @Bean
    Binding timeStampBinding(org.springframework.amqp.core.Queue timeStampQueue, DirectExchange exchange) {

        return BindingBuilder.bind(timeStampQueue).to(exchange).with(env.getProperty("rabbitmq.routingkey.timestamp"));
    }

    @Bean
    public MappingJackson2MessageConverter jackson2Converter() {

        return new MappingJackson2MessageConverter();
    }
}
