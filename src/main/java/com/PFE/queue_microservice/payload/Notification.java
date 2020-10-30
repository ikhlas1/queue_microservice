package com.PFE.queue_microservice.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {

    private String serviceName;
    private String queueName;
    private String contactInfo;
    private String subject;
    private String msgContent;

    public Notification(){

    }

    public Notification(String serviceName, String queueName, String contactInfo, String subject, String msgContent) {
        this.serviceName = serviceName;
        this.queueName = queueName;
        this.contactInfo = contactInfo;
        this.subject = subject;
        this.msgContent = msgContent;
    }


}
