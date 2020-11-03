package com.PFE.queue_microservice.payload;

import com.PFE.queue_microservice.model.Client;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;


@Getter
@Setter
@RequiredArgsConstructor
public class TimeStamp {
    private String stampId;
    private String queueId;
    private String serviceId;
    //private int clientNumber; //the client's number in the queue
    private Client client;
    //private LocalDateTime timeStamp;
    private int year;
    private int month;
    private int dayOfMonth;
    private int hour;
    private int minute;
    private int second;
    private int nano;
    private String operationType; // Client is Added, Client is Done, Client is Late, Client's Turn.

    public TimeStamp(String queueId,
                     String serviceId,
                     Client client,
                     int year,
                     int month,
                     int dayOfMonth,
                     int hour,
                     int minute,
                     int second,
                     int nano,
                     String operationType) {
        this.queueId = queueId;
        this.serviceId = serviceId;
        this.client = client;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.nano = nano;
        this.operationType = operationType;
    }
}

