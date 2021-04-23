package com.PFE.queue_microservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueueForm {

    @NotBlank(message = "Queue name is mandatory")
    @Size(min = 1, max = 20, message = "Queue name's length must be greater than or equal to 1 and less than or equal to 20.")
    private String queueName;
    @NotBlank(message = "Queue size is mandatory")
    @Min(value = 5, message = "Queue size must be greater than or equal to 5.")
    @Max(value = 50, message = "Queue size must be less than or equal to 50.")
    @Digits(integer = 2, fraction = 0, message = "Invalid Queue size type.")
    private int queueSize;
    @NotBlank(message = "Notification factor is mandatory")
    @Digits(integer = 1, fraction = 0, message = "Invalid Notification Factor.")
    @Min(value = 1, message = "Notification factor must be greater than or equal to  1.")
    @Max(value = 5, message = "Notification factor must be less than or equal to 5.")
    private int notificationFactor;
}
