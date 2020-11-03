package com.PFE.queue_microservice.repository;

import com.PFE.queue_microservice.model.Queue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueueRepository extends MongoRepository <Queue,Integer> {

    List<Queue> findByServiceId(String serviceId);

    List<Queue> findByServiceName(String serviceName);

    Queue findByQueueId(String queueId);

    Queue findByQueueName(String queueName);
}
