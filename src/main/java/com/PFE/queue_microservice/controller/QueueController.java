package com.PFE.queue_microservice.controller;

import com.PFE.queue_microservice.DTO.ClientForm;
import com.PFE.queue_microservice.DTO.QueueForm;
import com.PFE.queue_microservice.model.Client;
import com.PFE.queue_microservice.model.Queue;
import com.PFE.queue_microservice.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping ("/queue")
public class QueueController {

    @Autowired
    private QueueService queueService;


    @GetMapping ("/getQueue/{id}")
    public ResponseEntity<Queue> getQueue(@Valid @NotBlank(message = "Invalid Queue ID.") @PathVariable("id") String queueId,
                                          @Valid @NotBlank(message = "Unauthorized request.") @RequestParam(name = "sub")String serviceId){

        return queueService.findByQueueId(queueId, serviceId);
    }

    @GetMapping ("/getQueues")
    public ResponseEntity<List<Queue>> getQueues(@Valid @NotBlank(message = "Unauthorized request.") @RequestParam(name = "sub") String serviceId){

        return queueService.findByServiceId(serviceId);
    }

    @PutMapping ("/updateQueue/{id}")
    public ResponseEntity<Queue> updateQueue(@Valid @RequestBody QueueForm queueForm,
                                             @Valid @NotBlank(message = "Invalid Queue ID.") @PathVariable("id")String queueId,
                                             @Valid @NotBlank(message = "Unauthorized request.") @RequestParam(name = "sub")String serviceId){

        return queueService.updateQueue(queueForm, queueId, serviceId);
    }

    @PostMapping ("/addQueue")
    public ResponseEntity<Queue> addQueue(@Valid @RequestBody QueueForm queueForm,
                                          @Valid @NotBlank(message = "Unauthorized request.") @RequestParam(name = "sub")String serviceId,
                                          @Valid @NotBlank(message = "Unauthorized request.") @RequestParam(name = "preferred_username")String serviceName){

        Queue queue = new Queue(queueForm.getQueueName(),
                queueForm.getQueueSize(),
                queueForm.getNotificationFactor(),
                serviceName, serviceId );
        return queueService.addQueue(queue);
    }

    @PutMapping ("/addClient/{id}")
    public ResponseEntity<Queue> addClientToQueue (@RequestBody Client clientForm,
                                                   @RequestParam(name = "sub")String serviceId,
                                                   @PathVariable("id")String queueId){

        return queueService.addClient(clientForm, serviceId, queueId);
    }

    @PutMapping ("/deleteClient/{id}")
    public ResponseEntity<Queue> deleteClient (@RequestBody ClientForm clientForm,
                                               @RequestParam(name = "sub")String serviceId,
                                               @PathVariable("id")String queueId){

        return queueService.deleteClient(clientForm, serviceId, queueId);
    }

    @DeleteMapping ("/deleteQueue/{id}")
    public ResponseEntity<String> deleteQueue(@RequestParam("sub") String serviceId,
                                      @PathVariable("id") String queueId){

         return queueService.deleteQueue(serviceId, queueId);
    }

//    @DeleteMapping ("/deleteQueues")
//    public ResponseEntity<ArrayList<QueueDTO>> deleteQueues(@RequestParam ("serviceId")String serviceId){
//
//        return queueService.deleteQueues(serviceId);
//    }
}
