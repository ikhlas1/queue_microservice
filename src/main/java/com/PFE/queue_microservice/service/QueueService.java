package com.PFE.queue_microservice.service;

import com.PFE.queue_microservice.model.Queue;
import com.PFE.queue_microservice.repository.QueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueService {
    @Autowired
    QueueRepository queueRepository;

    public List<Queue> getAll() {
        return queueRepository.findAll();
    }

    public Queue addQueue(Queue q) {
        return queueRepository.insert(q);
    }

    public Queue updateQueue(Queue q) {
        return queueRepository.save(q);
    }

    public void delete(int id) {
        queueRepository.deleteById(id);
    }

    public List<Queue> findByServiceId(int serviceId) {
        return queueRepository.findByServiceId(serviceId);
    }

    public List<Queue> findByServiceName(String serviceName) {
        return queueRepository.findByServiceName(serviceName);

    }

    public Queue findByQueueId(int queueId) {
        return queueRepository.findByQueueId(queueId);
    }

    public Queue findByQueueName(String queueName) {
        return queueRepository.findByQueueName(queueName);
    }

    public Queue updateQueueSize(int queueId, int queueSize) {
        Queue q = new Queue();
        q = findByQueueId(queueId);
        q.setQueueSize(queueSize);
        return queueRepository.save(q);
    }

    public Queue updateQueueNotificationFactor(int queueId, int notificationFactor) {
        Queue q = new Queue();
        q = findByQueueId(queueId);
        q.setNotificationFactor(notificationFactor);
        return queueRepository.save(q);
    }

    public Queue updateQueueName(int queueId, String queueName) {
        Queue q = new Queue();
        q = findByQueueId(queueId);
        q.setQueueName(queueName);
        return queueRepository.save(q);
    }

    public Queue updateQueueState(int queueId, boolean queueState) {
        Queue q = new Queue();
        q = findByQueueId(queueId);
        q.setQueueState(queueState);
        return queueRepository.save(q);
    }

    public Queue updateQueueServiceName(int queueId, String queueServiceName) {
        Queue q = new Queue();
        q = findByQueueId(queueId);
        q.setServiceName(queueServiceName);
        return queueRepository.save(q);
    }
}
