package com.PFE.queue_microservice.rabbitmqconfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class ExchangeBindingsConfig {

    @Autowired
    Environment env;

    //Exchanges
    @Bean
    DirectExchange notificationExchange() {

        return new DirectExchange(env.getProperty("rabbitmq.exchange.notification"));
    }

    @Bean
    DirectExchange clientExchange() {

        return new DirectExchange(env.getProperty("rabbitmq.exchange.client"));
    }

    @Bean
    DirectExchange serviceExchange() {

        return new DirectExchange(env.getProperty("rabbitmq.exchange.service"));
    }

    //Queues
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
    org.springframework.amqp.core.Queue addQueue() {

        return new org.springframework.amqp.core.Queue(Objects.requireNonNull(env.getProperty("rabbitmq.queue.addqueue")), false);
    }

    @Bean
    org.springframework.amqp.core.Queue removeQueue() {

        return new org.springframework.amqp.core.Queue(Objects.requireNonNull(env.getProperty("rabbitmq.queue.removequeue")), false);
    }

    @Bean
    org.springframework.amqp.core.Queue removeService() {

        return new org.springframework.amqp.core.Queue(Objects.requireNonNull(env.getProperty("rabbitmq.queue.removeservice")), false);
    }

    //Bindings
    @Bean
    Binding addqueueBinding(org.springframework.amqp.core.Queue addQueue, DirectExchange serviceExchange) {

        return BindingBuilder.bind(addQueue).to(serviceExchange).with(env.getProperty("rabbitmq.routingkey.addqueue"));
    }

    @Bean
    Binding removequeueBinding(org.springframework.amqp.core.Queue removeQueue, DirectExchange serviceExchange) {

        return BindingBuilder.bind(removeQueue).to(serviceExchange).with(env.getProperty("rabbitmq.routingkey.removequeue"));
    }

    @Bean
    Binding turnBinding(org.springframework.amqp.core.Queue turnQueue, DirectExchange notificationExchange) {

        return BindingBuilder.bind(turnQueue).to(notificationExchange).with(env.getProperty("rabbitmq.routingkey.turn"));
    }

    @Bean
    Binding lateBinding(org.springframework.amqp.core.Queue lateQueue, DirectExchange notificationExchange) {

        return BindingBuilder.bind(lateQueue).to(notificationExchange).with(env.getProperty("rabbitmq.routingkey.late"));
    }

    @Bean
    Binding addedBinding(org.springframework.amqp.core.Queue addedQueue, DirectExchange notificationExchange) {

        return BindingBuilder.bind(addedQueue).to(notificationExchange).with(env.getProperty("rabbitmq.routingkey.added"));
    }

    @Bean
    Binding statusBinding(org.springframework.amqp.core.Queue statusQueue, DirectExchange notificationExchange) {

        return BindingBuilder.bind(statusQueue).to(notificationExchange).with(env.getProperty("rabbitmq.routingkey.status"));
    }

    @Bean
    Binding timeStampBinding(org.springframework.amqp.core.Queue timeStampQueue, DirectExchange clientExchange) {

        return BindingBuilder.bind(timeStampQueue).to(clientExchange).with(env.getProperty("rabbitmq.routingkey.timestamp"));
    }

    @Bean
    Binding removeserviceBinding(org.springframework.amqp.core.Queue removeService, DirectExchange serviceExchange) {

        return BindingBuilder.bind(removeService).to(serviceExchange).with(env.getProperty("rabbitmq.routingkey.removeservice"));
    }
}
