package com.PFE.queue_microservice.DTO;

import com.PFE.queue_microservice.model.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientForm {

    private Client client;
    @NotBlank(message = "A valid reason for deleting a client is required.")
    @Pattern(regexp = "cancelled|done|late", message = "Invalid reason. Valid reasons: done|late|cancelled.")
    private String reason;
}
