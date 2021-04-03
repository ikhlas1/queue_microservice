package com.PFE.queue_microservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Getter
@Setter
@RequiredArgsConstructor
@Document(collection = "Queue")
public class Queue {
    @Id
    private String queueId;
    private String queueName;
    private int queueSize;
    private int notificationFactor;
    private boolean queueState;//on or off
    private String serviceName;
    private String serviceId;
    private List<Client> clientQueue ;



    public boolean addClient (Client c){
        boolean clientAdded = false;

        if (clientQueue.size() < queueSize && queueState) //The queue isn't full and is available
        {
            clientAdded = clientQueue.add(c);
        } else if (clientQueue.size() >= queueSize && queueState) {
            //clientAdded = false;
            System.out.println("The queue"+queueName+" in service "+serviceName+" is full.");
        } else if (!queueState) {
            //clientAdded = false;
            System.out.println("The queue "+queueName+" in service "+serviceName+" is unavailable.");
        }

        return clientAdded;
    }

    public Client deleteClient (){

        Client deletedClient = new Client();

        if (!clientQueue.isEmpty() && queueState) {//The queue isn't empty and is available
            deletedClient = clientQueue.remove(0);
        } else if (clientQueue.isEmpty() && queueState) {
            System.out.println("The clients queue " + queueName + " in service " + serviceName + " is empty.");
        } else
            System.out.println("The queue" + queueName + " in service " + serviceName + " is unavailable.");

        return deletedClient;
    }

}