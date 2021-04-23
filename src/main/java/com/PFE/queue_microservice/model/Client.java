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
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;


//    public Client(int queueNumber, String firstName, String lastName, String phoneNumber, String emailAddress) {
//        this.queueNumber = queueNumber;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.phoneNumber = phoneNumber;
//        this.emailAddress = emailAddress;
//    }
//
    public boolean equals(Client client) {

        return (this.queueNumber == client.getQueueNumber()
                && this.phoneNumber.equals(client.getPhoneNumber())
                && this.emailAddress.equals(client.getEmailAddress()));
    }
}

