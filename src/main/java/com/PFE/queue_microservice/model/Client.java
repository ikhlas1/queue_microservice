package com.PFE.queue_microservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client {
    private int queueNumber;
    @NotNull(message = "Client's First name must not be null.")
    private String firstName;
    @NotNull(message = "Client's Family name must not be null.")
    private String lastName;
    @NotNull(message = "Client's phone number must not be null.")
    private String phoneNumber;
    @NotNull(message = "Client's email address must not be null.")
    private String emailAddress;


    public boolean equals(Client client) {

        return (this.queueNumber == client.getQueueNumber()
                && this.phoneNumber.equals(client.getPhoneNumber())
                && this.emailAddress.equals(client.getEmailAddress())
                && this.firstName.equals(client.getFirstName())
                && this.lastName.equals(client.getLastName())) ;
    }
}

