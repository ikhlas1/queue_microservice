package com.PFE.queue_microservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;


@Getter
@Setter
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


    public Queue(){

    }
    public void addClient (Client c){
        clientQueue.add(c);
    }
    public void deleteClient (){
        clientQueue.remove(0);
    }
}