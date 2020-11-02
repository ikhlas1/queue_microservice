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
        //Must notify clients of this update, or make sure the queue is offline (empty) before the update
        return queueService.updateQueueName(queueId,queueName);
    }

    @PutMapping("/updateQueueNotificationFactor")
    public Queue updateQueueNotificationFactor(@RequestParam(name = "queueId")int queueId,@RequestParam(name ="notificationFactor") int  notificationFactor){
        //Must notify clients of this update, or make sure the queue is offline (empty) before the update
        return queueService.updateQueueNotificationFactor(queueId,notificationFactor);
    }

    @PutMapping ("/updateQueueSize")
    public Queue updateQueueSize (@RequestParam(name = "queueId")int queueId,@RequestParam(name ="queueSize") int queueSize){
        return queueService.updateQueueSize(queueId,queueSize);
    }

    @PutMapping ("/updateQueueState")
    public Queue updateQueueState (@RequestParam(name = "queueId")int queueId,@RequestParam(name ="queueState") boolean queueState){
        //Send to rabbitmq-status-queue
        if (!queueState)
            queueService.generateStatusNotification(queueService.findByQueueId(queueId));
        return queueService.updateQueueState(queueId,queueState);
    }

    @PutMapping("/updateQueueServiceName")
    public Queue updateQueueServiceName(@RequestParam(name = "queueId")int queueId,@RequestParam(name ="queueServiceName") String queueServiceName){

        return queueService.updateQueueServiceName(queueId,queueServiceName);
    }

    /*
    @PutMapping ("/addClient")
    public Queue addClientToQueue (@RequestParam(name = "queueId")int queueId,@RequestBody Client c){
        Queue q;
        q = findByQueueId(queueId);
        //c.setQueueNumber(q.getClientQueue().get(q.getClientQueue().size()-1).getQueueNumber() + 1);
        q.addClient(c);
        return queueService.updateQueue(q);
    }*/
    @PutMapping ("/addClient")
    public Queue addClientToQueue (@RequestParam(name = "queueId")int queueId,
                                   @RequestParam(name = "phoneNumber")String phoneNumber,
                                   @RequestParam(name = "emailAddress")String emailAddress){

        Queue q = queueService.addClient(queueId, phoneNumber, emailAddress);
        LocalDateTime localDateTime;
        //Send to rabbitmq-added-queue
        queueService.generateAddedNotification(q);
        //Send to rabbitmq-timestamp-queue
        localDateTime = LocalDateTime.now();
        queueService.generateTimeStamp(q, q.getClientQueue().get(q.getClientQueue().size()-1),"Client is added.", localDateTime);

        q = queueService.updateQueue(q);
        return q;
    }

    /*@PutMapping ("/deleteClient")
    public Queue deleteClientFromQueue (@RequestParam(name = "queueId")int queueId){
        Queue q;
        q = findByQueueId(queueId);
        q.deleteClient();
        //Send to rabbitmq-turn-queue
        return queueService.updateQueue(q);
    }*/

    @PutMapping ("/deleteClient")
    public Queue deleteClientFromQueue (@RequestParam(name = "queueId")int queueId,
                                        @RequestParam(name = "reason")String reason){
        Queue q = findByQueueId(queueId);
        LocalDateTime localDateTime;

        if (!q.getClientQueue().isEmpty()) {
            if (reason.equals("late")) {
                //Send to rabbitmq-late-queue
                queueService.generateLateNotification(q);
                localDateTime = LocalDateTime.now();
                //Send to rabbitmq-timestamp-queue
                queueService.generateTimeStamp(q, q.getClientQueue().get(0),"Client is late.", localDateTime);
            } else if (reason.equals("done")) {
                //Send to rabbitmq-timestamp-queue
                localDateTime = LocalDateTime.now();
                queueService.generateTimeStamp(q, q.getClientQueue().get(0), "Client is done.", localDateTime);
            }
            //Delete the Client
            q = queueService.deleteClient(queueId);
            //Send to rabbitmq-turn-queue
            queueService.generateTurnNotification(q);
            localDateTime = LocalDateTime.now();
            //Send to rabbitmq-timestamp-queue
            queueService.generateTimeStamp(q, q.getClientQueue().get(0), "Client's turn.", localDateTime);
            //Send to rabbitmq-turn-queue
            if (q.getNotificationFactor() < q.getClientQueue().size())
                queueService.generateAlmostTurnNotification(q);

            //Update the QueueDB after client deletion
            q = queueService.updateQueue(q);
        }

        return q;
    }

    @DeleteMapping ("/deleteQueueById")
    public String deleteQueue(@RequestParam("id") int id){
         queueService.delete(id);
         return "Queue deleted successfully";
    }
}
