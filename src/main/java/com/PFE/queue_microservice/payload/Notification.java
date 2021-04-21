package com.PFE.queue_microservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    // To be deleted and we'll have  to change the code accordingly asap.
//    private String serviceName;
//    private String queueName;

    private String contactInfo;
    private String subject;
    private String msgContent;

    public boolean setContactInfo(String phoneNumber, String emailAddress){
        boolean set;

        if (!phoneNumber.equals("")){
            this.contactInfo = phoneNumber;
            set = true;
        } else if (!emailAddress.equals("")){
            this.contactInfo = emailAddress;
            set = true;
        } else
            set = false;

        return set;
    }


}
