package com.PFE.queue_microservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueueForm {

    private String queueName;
    private int queueSize;
    private int notificationFactor;
}
