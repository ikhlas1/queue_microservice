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
    private int queueId;
    private String queueName;
    private int queueSize;
    private int notificationFactor;
    private boolean queueState;//on or off
    private String serviceName;
    private int serviceId;
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

    public boolean deleteClient (){
        boolean clientDeleted = false;

        if (!clientQueue.isEmpty() && queueState) {//The queue isn't empty and is available
            clientQueue.remove(0);
            clientDeleted = true;
        } else if (clientQueue.isEmpty() && queueState) {
            System.out.println("The clients queue " + queueName + " in service " + serviceName + " is empty.");
        } else
            System.out.println("The queue" + queueName + " in service " + serviceName + " is unavailable.");

        return clientDeleted;
    }
}