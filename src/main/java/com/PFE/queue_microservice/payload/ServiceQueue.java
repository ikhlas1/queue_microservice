package com.PFE.queue_microservice.payload;

import com.sun.source.doctree.SerialDataTree;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public class ServiceQueue implements Serializable {

    private String serviceId;
    private String queueId;
    //Might change params if necessary

    public ServiceQueue(String serviceId, String queueId) {

        this.serviceId = serviceId;
        this.queueId = queueId;
    }

}
