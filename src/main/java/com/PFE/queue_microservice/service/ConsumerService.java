package com.PFE.queue_microservice.service;

import com.PFE.queue_microservice.repository.QueueRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @Autowired
    QueueRepository queueRepository;

    @RabbitListener(queues = "${rabbitmq.queue.removeservice}" )
    public void removeServiceQueues(String serviceId){
        queueRepository.findByServiceId(serviceId).forEach(queue ->
                queueRepository.deleteById(queue.getQueueId()));
    }
}
