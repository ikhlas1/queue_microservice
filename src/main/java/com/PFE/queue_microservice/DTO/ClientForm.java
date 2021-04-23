package com.PFE.queue_microservice.DTO;

import com.PFE.queue_microservice.model.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientForm {

    private Client client;
    private String reason;
}
