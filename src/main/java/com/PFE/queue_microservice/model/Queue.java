package com.PFE.queue_microservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Iterator;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Queue")
public class Queue {
    @Id
    private String queueId;
    private String queueName;
    private int queueSize;
    private int notificationFactor;
    private String serviceName;
    private String serviceId;
    private ArrayList<Client> clientQueue ;

    public Queue(String queueName, int queueSize, int notificationFactor, String serviceName, String serviceId) {
        this.queueName = queueName;
        this.queueSize = queueSize;
        this.notificationFactor = notificationFactor;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
    }


    public int addClient (String firstName, String lastName, String phoneNumber, String emailAddress){

        int addedClientIndex = -1;
        int lastClientIndex = clientQueue.size() - 1;

        if (clientQueue.size() == 0) { //Queue is empty
            clientQueue.add(new Client(1,
                    firstName,
                    lastName,
                    phoneNumber,
                    emailAddress));
            addedClientIndex = 0;
        } else if (clientQueue.size() < queueSize) { //Queue isn't full
            clientQueue.add(new Client(clientQueue.get(lastClientIndex).getQueueNumber() + 1,
                    firstName,
                    lastName,
                    phoneNumber,
                    emailAddress));
            addedClientIndex = lastClientIndex + 1;
        }

        return addedClientIndex;
    }

    public int deleteClient(Client client){

        int deletedClientIndex = -1;
        int index = -1;
        Client tempClient;
        int queueNumber = client.getQueueNumber();
        Iterator<Client> itr = clientQueue.iterator();

        while (itr.hasNext()) {
            index++;
            tempClient = itr.next();
            if (queueNumber == tempClient.getQueueNumber()) {
                itr.remove();
                deletedClientIndex = index;
                break;
            }
        }

        return deletedClientIndex;
    }
//    public Client deleteClient (){
//
//        Client deletedClient = new Client();
//
//        if (!clientQueue.isEmpty() && queueState) {//The queue isn't empty and is available
//            deletedClient = clientQueue.remove(0);
//        } else if (clientQueue.isEmpty() && queueState) {
//            System.out.println("The clients queue " + queueName + " in service " + serviceName + " is empty.");
//        }
//
//        return deletedClient;
//    }

}