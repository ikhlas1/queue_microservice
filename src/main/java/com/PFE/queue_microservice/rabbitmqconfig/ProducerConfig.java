package com.PFE.queue_microservice.rabbitmqconfig;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;



@Configuration
public class ProducerConfig{

    @Bean
    public MappingJackson2MessageConverter jackson2Converter() {

        return new MappingJackson2MessageConverter();
    }
}
