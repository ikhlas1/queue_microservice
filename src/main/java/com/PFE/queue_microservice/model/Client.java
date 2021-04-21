package com.PFE.queue_microservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client {
    private int queueNumber;
    private String phoneNumber;
    private String emailAddress;


    public boolean equals(Client client) {

        return (this.queueNumber == client.getQueueNumber()
                && this.phoneNumber.equals(client.getPhoneNumber())
                && this.emailAddress.equals(client.getEmailAddress()));
    }
}

