package com.PFE.queue_microservice.service;

import com.PFE.queue_microservice.model.Client;
import com.PFE.queue_microservice.model.Queue;
import com.PFE.queue_microservice.payload.Notification;
import com.PFE.queue_microservice.repository.QueueRepository;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class QueueService {
    @Autowired
    QueueRepository queueRepository;
    @Autowired
    private Environment env;
    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;
    @Autowired
    private MappingJackson2MessageConverter mappingJackson2MessageConverter;


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
        Queue q;
        q = findByQueueId(queueId);
        q.setQueueSize(queueSize);
        return queueRepository.save(q);
    }

    public Queue updateQueueNotificationFactor(int queueId, int notificationFactor) {
        Queue q;
        q = findByQueueId(queueId);
        q.setNotificationFactor(notificationFactor);
        return queueRepository.save(q);
    }

    public Queue updateQueueName(int queueId, String queueName) {
        Queue q;
        q = findByQueueId(queueId);
        q.setQueueName(queueName);
        return queueRepository.save(q);
    }

    public Queue updateQueueState(int queueId, boolean queueState) {
        Queue q;
        q = findByQueueId(queueId);
        q.setQueueState(queueState);
        return queueRepository.save(q);
    }

    public Queue updateQueueServiceName(int queueId, String queueServiceName) {
        Queue q;
        q = findByQueueId(queueId);
        q.setServiceName(queueServiceName);
        return queueRepository.save(q);
    }

    public Queue addClient(int queueId, String phoneNumber, String emailAddress){
        Client c;
        Queue q = findByQueueId(queueId);
        int clientQueueSize = q.getClientQueue().size();

        if (clientQueueSize == 0) {//Empty clients queue
            c = new Client(1, phoneNumber, emailAddress);
            q.addClient(c);
        } else if (clientQueueSize < q.getQueueSize()) {//Clients queue isn't full
            c = new Client(q.getClientQueue().get(q.getClientQueue().size() - 1).getQueueNumber() + 1, phoneNumber, emailAddress);
            q.addClient(c);
        } else
            System.out.printf("Can't add more clients because the queue %s in service %s is full.%n",q.getQueueName(), q.getServiceName());

        return q;
    }

    public Queue deleteClient(int queueId){
        Queue q = findByQueueId(queueId);
        q.deleteClient();

        return q;
    }

    public void generateTurnNotification(Queue queue) {
        String notificationCode = "turn";
        Notification notification = generateNotification(queue,null,notificationCode);
        rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
        if (!notification.getContactInfo().isEmpty())
        rabbitMessagingTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.name")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.turn")),
                notification);
    }

    public void generateAlmostTurnNotification(Queue queue) {
        String notificationCode = "almostTurn";
        Notification notification = generateNotification(queue,null,notificationCode);
        rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
        if (!notification.getContactInfo().isEmpty())
        rabbitMessagingTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.name")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.turn")),
                notification);
    }

    public void generateLateNotification(Queue queue) {
        String notificationCode = "late";
        Notification notification = generateNotification(queue,null,notificationCode);
        rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
        if (!notification.getContactInfo().isEmpty())
        rabbitMessagingTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.name")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.late")),
                notification);
    }

    public void generateAddedNotification(Queue queue) {
        String notificationCode = "added";
        Notification notification = generateNotification(queue,null,notificationCode);
        rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
        if (!notification.getContactInfo().isEmpty())
        rabbitMessagingTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.name")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.added")),
                notification);
    }

    public void generateStatusNotification(Queue queue) {
        String notificationCode = "status";
        int i = 0;

        rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
        while (i < queue.getClientQueue().size()) {
            Notification notification = generateNotification(queue, queue.getClientQueue().get(i), notificationCode);
            if (!notification.getContactInfo().isEmpty())
            rabbitMessagingTemplate.convertAndSend(
                    Objects.requireNonNull(env.getProperty("rabbitmq.exchange.name")),
                    Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.status")),
                    notification);
            i++;
        }
    }

    public Notification generateNotification(Queue queue, Client client, String notificationCode){
        String serviceName = queue.getServiceName();
        String queueName = queue.getQueueName();
        int notificationFactor = queue.getNotificationFactor();
        String clientPhoneNumber;
        String clientEmailAddress;
        int clientQueueSize = queue.getClientQueue().size();
        Notification notification = new Notification();

        notification.setServiceName(serviceName);
        notification.setQueueName(queueName);

        switch  (notificationCode){
            case "almostTurn": //Client's turn is almost up (based on NotificationFactor)
                if (notificationFactor < clientQueueSize) {
                    clientPhoneNumber = queue.getClientQueue().get(notificationFactor).getPhoneNumber();
                    clientEmailAddress = queue.getClientQueue().get(notificationFactor).getEmailAddress();
                    if (!clientPhoneNumber.isEmpty())
                        notification.setContactInfo(queue.getClientQueue().get(notificationFactor).getPhoneNumber());
                    else if (!clientEmailAddress.isEmpty())
                        notification.setContactInfo(queue.getClientQueue().get(notificationFactor).getEmailAddress());
                    else
                        notification.setContactInfo("");
                    notification.setSubject("Client's Turn Notification");
                    notification.setMsgContent("There are " + notificationFactor + " clients left before it's your turn.");
                }
                break;
            case "turn": //Client's turn is up
                clientPhoneNumber = queue.getClientQueue().get(0).getPhoneNumber();
                clientEmailAddress = queue.getClientQueue().get(0).getEmailAddress();
                if (!clientPhoneNumber.isEmpty())
                    notification.setContactInfo(clientPhoneNumber);
                else if (!clientEmailAddress.isEmpty())
                    notification.setContactInfo(clientEmailAddress);
                else
                    notification.setContactInfo("");
                notification.setSubject("Client's Turn Notification");
                notification.setMsgContent("It's your turn, please present yourself at the service "
                        +serviceName+"'s queue "+queueName+".");
                break;
            case "late": //Client is late, lost their turn
                clientPhoneNumber = queue.getClientQueue().get(0).getPhoneNumber();
                clientEmailAddress = queue.getClientQueue().get(0).getEmailAddress();
                if (!clientPhoneNumber.isEmpty())
                    notification.setContactInfo(clientPhoneNumber);
                else if (!clientEmailAddress.isEmpty())
                    notification.setContactInfo(clientEmailAddress);
                else
                    notification.setContactInfo("");
                notification.setSubject("Client's Late Notification");
                notification.setMsgContent("You're late!\nYou've been removed from the queue: "+ queueName +" in service: "+ serviceName);
                break;
            case "added": //Client added to the queue
                clientPhoneNumber = queue.getClientQueue().get(queue.getClientQueue().size() - 1).getPhoneNumber();
                clientEmailAddress = queue.getClientQueue().get(queue.getClientQueue().size() - 1).getEmailAddress();
                if (!clientPhoneNumber.isEmpty())
                    notification.setContactInfo(clientPhoneNumber);
                else if (!clientEmailAddress.isEmpty())
                    notification.setContactInfo(clientEmailAddress);
                else
                    notification.setContactInfo("");
                notification.setSubject("Client Added Notification");
                notification.setMsgContent(String.format("Added to the queue %s in service %s.\n" +
                        "Queue number: %d.\n" +
                        "You will be notified when there are %d clients before you.\n" +
                        "You will also be notified when it's your turn.",
                        queueName, serviceName,queue.getClientQueue().get(queue.getClientQueue().size() - 1).getQueueNumber() + 1, notificationFactor));
                break;
            case "status": //Queue status updated (true to false or vice versa)
                clientPhoneNumber = client.getPhoneNumber();
                clientEmailAddress = client.getEmailAddress();
                if (!clientPhoneNumber.isEmpty())
                    notification.setContactInfo(clientPhoneNumber);
                else if (!clientEmailAddress.isEmpty())
                    notification.setContactInfo(clientEmailAddress);
                else
                    notification.setContactInfo("");
                notification.setSubject("Queue Status Notification");
                if (!queue.isQueueState()) {
                    notification.setMsgContent(String.format("The queue %s in service %s is no longer available.\n" +
                            "You will be notified when it's available again.", queue.getQueueName(), queue.getServiceName()));
                } else
                    notification.setMsgContent(String.format("The queue %s in service %s is now available.\n",
                            queue.getQueueName(), queue.getServiceName()));

                break;
            default:
                break;
        }

        return notification;
    }
}
