package com.PFE.queue_microservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    private int queueNumber;
    private int phoneNumber;
    private String emailAddress;
}

