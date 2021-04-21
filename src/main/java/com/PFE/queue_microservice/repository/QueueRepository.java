package com.PFE.queue_microservice.repository;

import com.PFE.queue_microservice.model.Queue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface QueueRepository extends MongoRepository <Queue,String> {

    ArrayList<Queue> findByServiceId(String serviceId);

    Queue findByQueueId(String queueId);

    Queue findByQueueIdAndServiceId (String queueId, String serviceId);

    boolean existsByServiceId (String serviceId);

    boolean existsByQueueIdAndServiceId (String queueId, String serviceId);

    void deleteQueueByQueueIdAndServiceId (String queueId, String serviceId);

    void deleteQueuesByServiceId (String serviceId);
}
