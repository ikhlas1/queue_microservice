package com.PFE.queue_microservice.payload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class TimeStamp {
    private int stampId;
    private int queueId;
    private int serviceId;
    private int clientNumber; //the client's number in the queue
    private String timeStamp;
    private String operationType; // add, delete, late, your turn.

}

