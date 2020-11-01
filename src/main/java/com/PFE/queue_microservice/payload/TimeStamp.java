package com.PFE.queue_microservice.payload;

import com.PFE.queue_microservice.model.Client;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@RequiredArgsConstructor
public class TimeStamp {
    private int stampId;
    private int queueId;
    private int serviceId;
    //private int clientNumber; //the client's number in the queue
    private Client client;
    private LocalDateTime timeStamp;
    private String operationType; // Client is Added, Client is Done, Client is Late, Client's Turn.

}

