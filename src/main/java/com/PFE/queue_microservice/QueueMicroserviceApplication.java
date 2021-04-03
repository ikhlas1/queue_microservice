package com.PFE.queue_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class QueueMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueueMicroserviceApplication.class, args);
	}

}
