package com.PFE.queue_microservice.controller;

import com.PFE.queue_microservice.model.Client;
import com.PFE.queue_microservice.model.Queue;
import com.PFE.queue_microservice.repository.QueueRepository;
import com.PFE.queue_microservice.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
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
    public Queue findByQueueId(@RequestParam(name = "queueId") int queueId){
        return queueService.findByQueueId(queueId);
    }

    @GetMapping ("/getByQueueName")
    public Queue findByQueueName(@RequestParam(name = "queueName") String queueName){
        return queueService.findByQueueName(queueName);
    }

    @GetMapping ("/getByServiceId")
    public List<Queue> findByServiceId(@RequestParam(name = "serviceId") int serviceId){
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
    public Queue updateQueueName(@RequestParam(name = "queueId")int queueId,@RequestParam(name = "queueName") String queueName){

        return queueService.updateQueueName(queueId,queueName);
    }

    @PutMapping("/updateQueueNotificationFactor")
    public Queue updateQueueNotificationFactor(@RequestParam(name = "queueId")int queueId,@RequestParam(name ="notificationFactor") int  notificationFactor){

        return queueService.updateQueueNotificationFactor(queueId,notificationFactor);
    }

    @PutMapping ("/updateQueueSize")
    public Queue updateQueueSize (@RequestParam(name = "queueId")int queueId,@RequestParam(name ="queueSize") int queueSize){
        return queueService.updateQueueSize(queueId,queueSize);
    }

    @PutMapping ("/updateQueueState")
    public Queue updateQueueState (@RequestParam(name = "queueId")int queueId,@RequestParam(name ="queueState") boolean queueState){
        return queueService.updateQueueState(queueId,queueState);
    }

    @PutMapping("/updateQueueServiceName")
    public Queue updateQueueServiceName(@RequestParam(name = "queueId")int queueId,@RequestParam(name ="queueServiceName") String queueServiceName){

        return queueService.updateQueueServiceName(queueId,queueServiceName);
    }

    @PutMapping ("/addClient")
    public Queue addClientToQueue (@RequestParam(name = "queueId")int queueId,@RequestBody Client c){
        Queue q;
        q = findByQueueId(queueId);
        q.addClient(c);
        return queueService.updateQueue(q);
    }

    @PutMapping ("/deleteClient")
    public Queue deleteClientFromQueue (@RequestParam(name = "queueId")int queueId){
        Queue q;
        q = findByQueueId(queueId);
        q.deleteClient();
        return queueService.updateQueue(q);
    }

    @DeleteMapping ("/deleteQueueById")
    public String deleteQueue(@RequestParam("id") int id){
         queueService.delete(id);
         return "Queue deleted successfully";
    }
}
