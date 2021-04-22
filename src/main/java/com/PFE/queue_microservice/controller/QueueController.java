package com.PFE.queue_microservice.controller;

import com.PFE.queue_microservice.DTO.ClientForm;
import com.PFE.queue_microservice.DTO.QueueForm;
import com.PFE.queue_microservice.model.Queue;
import com.PFE.queue_microservice.DTO.QueueDTO;
import com.PFE.queue_microservice.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping ("/queue")
public class QueueController {

    @Autowired
    private QueueService queueService;


    @GetMapping ("/getQueue/{id}")
    public ResponseEntity<Queue> getQueue(@PathVariable("id") String queueId,
                                               @RequestParam(name = "sub")String serviceId){

        return queueService.findByQueueId(queueId, serviceId);
    }

//    @GetMapping ("/getByQueueName")
//    public Queue findByQueueName(@RequestParam(name = "queueName") String queueName){
//        return queueService.findByQueueName(queueName);
//    }

    @GetMapping ("/getQueues")
    public ResponseEntity<List<Queue>> getQueues(@RequestParam(name = "sub") String serviceId){

        return queueService.findByServiceId(serviceId);
    }


    @PutMapping ("/updateQueue")
    public Queue updateQueue(@RequestBody Queue q){

        return queueService.updateQueue(q);
    }

    @PutMapping("/updateQueueName")
    public ResponseEntity<Queue> updateQueueName(@RequestParam(name = "serviceId")String serviceId,
                                                 @RequestParam(name = "queueId")String queueId,
                                                 @RequestParam(name = "queueName") String queueName){
        //Only update if queue empty
        return queueService.updateQueueName(serviceId, queueId, queueName);
    }

    @PutMapping("/updateQueueNotificationFactor")
    public ResponseEntity<Queue> updateQueueNotificationFactor(@RequestParam(name = "serviceId")String serviceId,
                                                               @RequestParam(name = "queueId")String queueId,
                                                               @RequestParam(name ="notificationFactor") int  notificationFactor){

        return queueService.updateQueueNotificationFactor(serviceId, queueId,notificationFactor);
    }

    @PutMapping ("/updateQueueSize")
    public ResponseEntity<Queue> updateQueueSize (@RequestParam(name = "serviceId")String serviceId,
                                                  @RequestParam(name = "queueId")String queueId,
                                                  @RequestParam(name ="queueSize") int queueSize){
        return queueService.updateQueueSize(serviceId, queueId, queueSize);
    }

    @PostMapping ("/addQueue")
    public ResponseEntity<Queue> addQueue(@RequestBody QueueForm queueForm,
                                          @RequestParam(name = "sub")String serviceId,
                                          @RequestParam(name = "preferred_username")String serviceName){

        Queue queue = new Queue(queueForm.getQueueName(),
                queueForm.getQueueSize(),
                queueForm.getNotificationFactor(),
                serviceName, serviceId );
        return queueService.addQueue(queue);
    }

    @PutMapping ("/addClient")
    public ResponseEntity<Queue> addClientToQueue (@RequestBody ClientForm clientForm){
        return queueService.addClient(clientForm);
    }

    @PutMapping ("/deleteClient")
    public ResponseEntity<Queue> deleteClient (@RequestBody ClientForm clientForm){

        return queueService.deleteClient(clientForm);
    }

    @DeleteMapping ("/deleteQueueById")
    public ResponseEntity<QueueDTO> deleteQueue(@RequestParam("serviceId") String serviceId,
                                              @RequestParam("queueId") String queueId){

         return queueService.deleteQueue(serviceId, queueId);
    }

    @DeleteMapping ("/deleteQueues")
    public ResponseEntity<ArrayList<QueueDTO>> deleteQueues(@RequestParam ("serviceId")String serviceId){

        return queueService.deleteQueues(serviceId);
    }
}
