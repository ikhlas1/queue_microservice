package com.PFE.queue_microservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    private int queueNumber;
    private String phoneNumber;
    private String emailAddress;

    public Client(int queueNumber, String phoneNumber, String emailAddress) {
        this.queueNumber = queueNumber;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }
}

