package com.PFE.queue_microservice.service;

import com.PFE.queue_microservice.model.Client;
import com.PFE.queue_microservice.model.Queue;
import com.PFE.queue_microservice.payload.Notification;
import com.PFE.queue_microservice.payload.TimeStamp;
import com.PFE.queue_microservice.repository.QueueRepository;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        q = queueRepository.insert(q);
        return q;
    }

    public Queue updateQueue(Queue q) {

        return queueRepository.save(q);
    }

    public void deleteQueue(String id) {

        queueRepository.deleteById(id);
    }

    public List<Queue> findByServiceId(String serviceId) {

        return queueRepository.findByServiceId(serviceId);
    }

    public List<Queue> findByServiceName(String serviceName) {

        return queueRepository.findByServiceName(serviceName);
    }

    public Queue findByQueueId(String queueId) {

        return queueRepository.findByQueueId(queueId);
    }

    public Queue findByQueueName(String queueName) {

        return queueRepository.findByQueueName(queueName);
    }

    public Queue updateQueueSize(String queueId, int queueSize) {
        Queue q;
        q = findByQueueId(queueId);
        q.setQueueSize(queueSize);
        return queueRepository.save(q);
    }

    public Queue updateQueueNotificationFactor(String queueId, int notificationFactor) {
        Queue q;
        q = findByQueueId(queueId);
        q.setNotificationFactor(notificationFactor);
        return queueRepository.save(q);
    }

    public Queue updateQueueName(String queueId, String queueName) {
        Queue q;
        q = findByQueueId(queueId);
        q.setQueueName(queueName);
        return queueRepository.save(q);
    }

    public Queue updateQueueState(String queueId, boolean queueState) {

        Queue q;
        q = findByQueueId(queueId);
        if (!queueState) {
            this.generateStatusNotification(q);
            //delete clients or leave them?
        }
        q.setQueueState(queueState);
        return queueRepository.save(q);
    }

    public Queue updateQueueServiceName(String queueId, String queueServiceName) {
        Queue q;
        q = findByQueueId(queueId);
        q.setServiceName(queueServiceName);
        return queueRepository.save(q);
    }

    public Queue addClient(String queueId, String phoneNumber, String emailAddress){
        Client c;
        Queue q = findByQueueId(queueId);
        int clientQueueSize = q.getClientQueue().size();

        if (clientQueueSize == 0) {//Empty clients queue
            c = new Client(1, phoneNumber, emailAddress);
            q.addClient(c);
            LocalDateTime localDateTime;
            //Send to rabbitmq-added-queue
            generateAddedNotification(q);
            //Send to rabbitmq-timestamp-queue
            localDateTime = LocalDateTime.now();
            generateTimeStamp(q, q.getClientQueue().get(q.getClientQueue().size()-1),"added", localDateTime);
            //Update the queue with the added client
            q = queueRepository.save(q);
        } else if (clientQueueSize < q.getQueueSize()) {//Clients queue isn't full
            c = new Client(q.getClientQueue().get(q.getClientQueue().size() - 1).getQueueNumber() + 1, phoneNumber, emailAddress);
            q.addClient(c);
            LocalDateTime localDateTime;
            //Send to rabbitmq-added-queue
            generateAddedNotification(q);
            //Send to rabbitmq-timestamp-queue
            localDateTime = LocalDateTime.now();
            generateTimeStamp(q, q.getClientQueue().get(q.getClientQueue().size()-1),"added", localDateTime);
            //Update the queue with the added client
            q = queueRepository.save(q);
        } else
            System.out.printf("Queue %s is full.",q.getQueueName());

        return q;
    }

    public Queue deleteClient(String queueId, String reason){
        Queue q = findByQueueId(queueId);
        LocalDateTime localDateTime;
        Client deletedClient;

        deletedClient = q.deleteClient();
        //Update the QueueDB after client deletion
        q = queueRepository.save(q);

        if (reason.equals("late")) {
            //Send to rabbitmq-late-queue
            generateLateNotification(q, deletedClient);
            localDateTime = LocalDateTime.now();
            //Send to rabbitmq-timestamp-queue
            generateTimeStamp(q, deletedClient,"late", localDateTime);
        } else if (reason.equals("done")) {
            //Send to rabbitmq-timestamp-queue
            localDateTime = LocalDateTime.now();
            generateTimeStamp(q, deletedClient, "done", localDateTime);
        }
        if (!q.getClientQueue().isEmpty()) {
            //Send to rabbitmq-turn-queue
            generateTurnNotification(q);
            localDateTime = LocalDateTime.now();
            //Send to rabbitmq-timestamp-queue
            generateTimeStamp(q, q.getClientQueue().get(0), "turn", localDateTime);
            //Send to rabbitmq-turn-queue
            if (q.getNotificationFactor() < q.getClientQueue().size())
                generateAlmostTurnNotification(q);
        }

        return q;
    }

    public void generateTurnNotification(Queue queue) {
        String notificationCode = "turn";
        Notification notification = generateNotification(queue,null,notificationCode);
        rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
        if (!notification.getContactInfo().isEmpty())
        rabbitMessagingTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.notification")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.turn")),
                notification);
    }

    public void generateAlmostTurnNotification(Queue queue) {
        String notificationCode = "almostTurn";
        Notification notification = generateNotification(queue,null,notificationCode);
        rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
        if (!notification.getContactInfo().isEmpty())
        rabbitMessagingTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.notification")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.turn")),
                notification);
    }

    public void generateLateNotification(Queue queue, Client client) {
        String notificationCode = "late";
        Notification notification = generateNotification(queue,client,notificationCode);
        rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
        if (!notification.getContactInfo().isEmpty())
        rabbitMessagingTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.notification")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.late")),
                notification);
    }

    public void generateAddedNotification(Queue queue) {
        String notificationCode = "added";
        Notification notification = generateNotification(queue,null,notificationCode);
        rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
        if (!notification.getContactInfo().isEmpty())
        rabbitMessagingTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.notification")),
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
                    Objects.requireNonNull(env.getProperty("rabbitmq.exchange.notification")),
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
        Notification notification = new Notification();

        notification.setServiceName(serviceName);
        notification.setQueueName(queueName);

        switch  (notificationCode){
            case "almostTurn": //Client's turn is almost up (based on NotificationFactor)
                    clientPhoneNumber = queue.getClientQueue().get(notificationFactor).getPhoneNumber();
                    clientEmailAddress = queue.getClientQueue().get(notificationFactor).getEmailAddress();
                    if (!clientPhoneNumber.isEmpty())
                        notification.setContactInfo(clientPhoneNumber);
                    else if (!clientEmailAddress.isEmpty())
                        notification.setContactInfo(clientEmailAddress);
                    else
                        notification.setContactInfo("");
                    notification.setSubject(serviceName+" - "+queueName);
                    notification.setMsgContent(notificationFactor + " Clients left before you");
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
                notification.setSubject(serviceName+" - "+queueName);
                notification.setMsgContent("It's your turn, please present yourself at the reception.");
                break;
            case "late": //Client is late, lost their turn
                clientPhoneNumber = client.getPhoneNumber();
                clientEmailAddress = client.getEmailAddress();
                if (!clientPhoneNumber.isEmpty())
                    notification.setContactInfo(clientPhoneNumber);
                else if (!clientEmailAddress.isEmpty())
                    notification.setContactInfo(clientEmailAddress);
                else
                    notification.setContactInfo("");
                notification.setSubject(serviceName+" - "+queueName);
                notification.setMsgContent("Sorry, you've been removed for being late.");
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
                notification.setSubject(serviceName+" - "+queueName);
                notification.setMsgContent(String.format("Current number: %d.\n"+
                        "Your number: %d.\n" +
                        "You will be notified when there are %d clients before you, " +
                        "and when it's your turn.",queue.getClientQueue().get(0).getQueueNumber(),queue.getClientQueue().get(queue.getClientQueue().size() - 1).getQueueNumber(), notificationFactor));
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
                notification.setSubject(serviceName+" - "+queueName);
                if (!queue.isQueueState()) {
                    notification.setMsgContent("The queue is temporarily unavailable." +
                            "You will be notified when it's up again.");
                } else
                    notification.setMsgContent("The queue is available again.");

                break;
            default:
                break;
        }

        return notification;
    }

    public void generateTimeStamp(Queue queue, Client client, String operationType, LocalDateTime localDateTime) {

        TimeStamp timeStamp = new TimeStamp(queue.getQueueId(),
                queue.getServiceId(),
                client,
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth(),
                localDateTime.getHour(),
                localDateTime.getMinute(),
                localDateTime.getSecond(),
                localDateTime.getNano(),
                operationType);
        //timeStamp.setStampId("");
        rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
        rabbitMessagingTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.notification")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.timestamp")),
                timeStamp);
    }
}
