package com.PFE.queue_microservice.controller;

import com.PFE.queue_microservice.model.Queue;
import com.PFE.queue_microservice.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping ("/queue")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @GetMapping ("/getAll")
    public List<Queue> getAll(){

        return queueService.getAll();
    }

    @GetMapping ("/getByQueueId")
    public Queue findByQueueId(@RequestParam(name = "queueId") String queueId){
        return queueService.findByQueueId(queueId);
    }

    @GetMapping ("/getByQueueName")
    public Queue findByQueueName(@RequestParam(name = "queueName") String queueName){
        return queueService.findByQueueName(queueName);
    }

    @GetMapping ("/getByServiceId")
    public List<Queue> findByServiceId(@RequestParam(name = "serviceId") String serviceId){
        return queueService.findByServiceId(serviceId);
    }

    @GetMapping ("/getByServiceName")
    public List<Queue> findByServiceName(@RequestParam(name = "serviceName") String serviceName){
        return queueService.findByServiceName(serviceName);
    }

    @PostMapping ("/addQueue")
    public Queue addQueue(@RequestBody Queue q){

        return queueService.addQueue(q);
    }

   /* @PutMapping ("/updateQueue")
    public Queue updateQueue(@RequestBody Queue q){

        return queueService.updateQueue(q);
    }*/

    @PutMapping("/updateQueueName")
    public Queue updateQueueName(@RequestParam(name = "queueId")String queueId,@RequestParam(name = "queueName") String queueName){
        //Must notify clients of this update, or make sure the queue is offline (empty) before the update
        return queueService.updateQueueName(queueId,queueName);
    }

    @PutMapping("/updateQueueNotificationFactor")
    public Queue updateQueueNotificationFactor(@RequestParam(name = "queueId")String queueId,@RequestParam(name ="notificationFactor") int  notificationFactor){
        //Must notify clients of this update, or make sure the queue is offline (empty) before the update
        return queueService.updateQueueNotificationFactor(queueId,notificationFactor);
    }

    @PutMapping ("/updateQueueSize")
    public Queue updateQueueSize (@RequestParam(name = "queueId")String queueId,@RequestParam(name ="queueSize") int queueSize){
        return queueService.updateQueueSize(queueId,queueSize);
    }

    @PutMapping ("/updateQueueState")
    public Queue updateQueueState (@RequestParam(name = "queueId")String queueId,@RequestParam(name ="queueState") boolean queueState){
        //Send to rabbitmq-status-queue
        return queueService.updateQueueState(queueId,queueState);
    }

    @PutMapping("/updateQueueServiceName")
    public Queue updateQueueServiceName(@RequestParam(name = "queueId")String queueId,@RequestParam(name ="queueServiceName") String queueServiceName){

        return queueService.updateQueueServiceName(queueId,queueServiceName);
    }

    @PutMapping ("/addClient")
    public Queue addClientToQueue (@RequestParam(name = "queueId")String queueId,
                                   @RequestParam(name = "phoneNumber")String phoneNumber,
                                   @RequestParam(name = "emailAddress")String emailAddress){

        Queue q = queueService.addClient(queueId, phoneNumber, emailAddress);

        return q;
    }

    @PutMapping ("/deleteClient")
    public Queue deleteClientFromQueue (@RequestParam(name = "queueId")String queueId,
                                        @RequestParam(name = "reason")String reason){

        return queueService.deleteClient(queueId, reason);
    }

    @DeleteMapping ("/deleteQueueById")
    public String deleteQueue(@RequestParam("id") int id){
         queueService.delete(id);
         return "Queue deleted successfully";
    }
}
