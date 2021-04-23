package com.PFE.queue_microservice.service;

import com.PFE.queue_microservice.DTO.ClientForm;
import com.PFE.queue_microservice.DTO.QueueForm;
import com.PFE.queue_microservice.model.Client;
import com.PFE.queue_microservice.model.Queue;
import com.PFE.queue_microservice.DTO.QueueDTO;
import com.PFE.queue_microservice.model.Reason;
import com.PFE.queue_microservice.payload.Notification;
import com.PFE.queue_microservice.payload.TimeStamp;
import com.PFE.queue_microservice.repository.QueueRepository;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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


    public ResponseEntity<Queue> findByQueueId(String queueId, String serviceId) {

        if (queueRepository.existsByQueueIdAndServiceId(queueId, serviceId)){
            return  ResponseEntity.status(HttpStatus.OK)
                    .body(queueRepository.findByQueueIdAndServiceId(queueId, serviceId));
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Queue not found.");

    }

    public ResponseEntity<List<Queue>> findByServiceId(String serviceId) {

        if (queueRepository.existsByServiceId(serviceId)){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(queueRepository.findByServiceId(serviceId));
        } else {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Not queues found.");
        }
    }

    public ResponseEntity<Queue> updateQueue(QueueForm queueForm, String queueId, String serviceId) {
        Queue queue;

        if (queueRepository.existsByQueueIdAndServiceId(queueId, serviceId)){
            queue = queueRepository.findByQueueIdAndServiceId(queueId, serviceId);
            if (queue.getClientQueue().size() == 0
                    && (!queueRepository.existsByQueueNameAndServiceId(queueForm.getQueueName(), serviceId)
                    || queueForm.getQueueName().equals(queue.getQueueName()))){

                queue.setQueueName((queueForm.getQueueName()));
                queue.setQueueSize(queueForm.getQueueSize());
                queue.setNotificationFactor(queueForm.getNotificationFactor());
                queue = queueRepository.save(queue);

                return ResponseEntity.status(HttpStatus.OK)
                        .body(queue);
            }
            else if (queue.getClientQueue().size() !=0){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Queue isn't empty.");
            } else
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Queue name already exists.");
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Queue not found.");
        }
    }

    public ResponseEntity<Queue> addQueue(Queue queue) {

        if (queueRepository.existsByQueueNameAndServiceId(queue.getQueueName(), queue.getServiceId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Queue name already exists.");
        } else {
            queue.setClientQueue(new ArrayList<>());
            queue = queueRepository.insert(queue);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(queue);
        }
    }

    public ResponseEntity deleteQueue(String serviceId, String queueId) {
        Queue queue;

        if (queueRepository.existsByQueueIdAndServiceId(queueId, serviceId)){
            queue = queueRepository.findByQueueIdAndServiceId(queueId, serviceId);
            if (queue.getClientQueue().size()==0){
                queueRepository.deleteQueueByQueueIdAndServiceId(queueId, serviceId);

                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Clients found in the queue. Cannot delete non-empty queues.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Queue not found.");
        }

    }

    public ResponseEntity<Queue> addClient (Client client, String serviceId, String queueId){

        Queue queue;
        int addedClientIndex;
        String firstName, lastName, phoneNumber, emailAddress;

        if (queueRepository.existsByQueueIdAndServiceId(queueId, serviceId)){
            firstName = client.getFirstName();
            lastName = client.getLastName();
            phoneNumber = client.getPhoneNumber();
            emailAddress = client.getEmailAddress();
            boolean validPN = validPhoneNumber(phoneNumber);
            boolean validEA = validEmailAddress(emailAddress);
            //Queue exists for valid service
            queue = queueRepository.findByQueueIdAndServiceId(queueId, serviceId);
            if (validEA && validPN){
                //Valid contact info
                addedClientIndex = queue.addClient(firstName, lastName, phoneNumber, emailAddress);
                if (addedClientIndex != -1){
                    //Client added
                    //Update the queue database with the added client
                    queue = queueRepository.save(queue);
                    //Generate TimeStamp
                    LocalDateTime localDateTime = LocalDateTime.now();
                    //Retrieve added client
                    client = queue.getClientQueue().get(addedClientIndex);
                    //Send to rabbitmq-timestamp-queue
                    //Send to rabbitmq-added-queue
                    sendNotificationAndTimestamp(queue, addedClientIndex, client, Reason.ADDED.getValue(), localDateTime);

                    return ResponseEntity.status(HttpStatus.OK)
                            .body(queue);
                } else {
                    //Queue is full (reached MAX CAPACITY)
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Queue is full, cannot add clients.");
                }
            } else if (!validEA)
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Email Address.");
              else
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Phone Number.");
        } else {
            //Queue or Service not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Queue not found.");
        }
    }

    private boolean validEmailAddress(String emailAddress) {
        boolean valid;

        // Using RFC5322
        String pattern = "(?im)^(?=.{1,64}@)(?:(\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"@)|((?:[0-9a-z](?:\\.(?!\\.)|[-!#\\$%&'\\*\\+/=\\?\\^`\\{\\}\\|~\\w])*)?[0-9a-z]@))(?=.{1,255}$)(?:(\\[(?:\\d{1,3}\\.){3}\\d{1,3}\\])|((?:(?=.{1,63}\\.)[0-9a-z][-\\w]*[0-9a-z]*\\.)+[a-z0-9][\\-a-z0-9]{0,22}[a-z0-9])|((?=.{1,63}$)[0-9a-z][-\\w]*))$";
        valid = emailAddress.equals("") || emailAddress.matches(pattern);

        return valid;
    }

    private boolean validPhoneNumber(String phoneNumber) {
        boolean valid;

        String pattern = "^213[567]\\d{8}$";
        valid = phoneNumber.equals("") || phoneNumber.matches(pattern);

        return  valid;
    }

    public ResponseEntity<Queue> deleteClient(ClientForm clientForm, String serviceId, String queueId) {

        Client targetClient = clientForm.getClient();
        String reason = clientForm.getReason();

        Queue queue;
        LocalDateTime localDateTime;
        int targetClientIndex;

        if (queueRepository.existsByQueueIdAndServiceId(queueId, serviceId)){
            queue = queueRepository.findByQueueIdAndServiceId(queueId, serviceId);
            if (!queue.getClientQueue().isEmpty()){
                //Queue isn't empty
                targetClientIndex = queue.deleteClient(targetClient);
                System.out.println(queue.getClientQueue().stream());
                if (targetClientIndex != -1){
                    //Client successfully deleted
                    queueRepository.save(queue);
                    //Notifications management
                    localDateTime = LocalDateTime.now();
                    sendNotificationsAndTimestamps(queue, targetClientIndex, targetClient, reason, localDateTime);

                    return ResponseEntity.status(HttpStatus.OK)
                            .body(queue);
                } else {
                    //Client not found
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found.");
                }
            } else {
                //Queue is empty
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Queue is empty.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Queue not found.");
        }
    }

    public ResponseEntity<ArrayList<QueueDTO>> deleteQueues(String serviceId) {
        // return deleted queues IDs
        AtomicBoolean notEmpty = new AtomicBoolean(false);
        ArrayList<QueueDTO> queueDTOS = new ArrayList<>();

        if (queueRepository.existsByServiceId(serviceId)){
            queueRepository.findByServiceId(serviceId).forEach(queue -> {
                if (queue.getClientQueue().size()==0){
                    // Queue is empty
                    queueRepository.deleteById(queue.getQueueId());
                    queueDTOS.add(new QueueDTO(queue.getQueueId(),true));
                } else {
                    // Queue has some clients
                    notEmpty.set(true);
                    queueDTOS.add(new QueueDTO(queue.getQueueId(),false));
                }
            });
            if (notEmpty.get())
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Clients found, cannot delete non-empty queues.");
            else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(queueDTOS);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No queues found");
        }
    }

    public void sendNotificationsAndTimestamps (Queue queue,
                                                int clientIndex,
                                                Client client,
                                                String reason,
                                                LocalDateTime localDateTime){

        if (reason.equals(Reason.LATE.getValue()) ||
                reason.equals(Reason.DONE.getValue()) ||
                reason.equals(Reason.CANCELLED.getValue())){

            sendNotificationAndTimestamp(queue, clientIndex, client, reason, localDateTime);
            if (clientIndex == 0){
                //Deleted first client, must generate TURN and ALMOST_TURN notifications/timestamps
                sendNotificationAndTimestamp(queue,0, null, Reason.TURN.getValue(), localDateTime);
                sendNotificationAndTimestamp(queue, 0, null, Reason.ALMOST_TURN.getValue(), localDateTime);
            } else if ( clientIndex <= queue.getNotificationFactor()){
                //Deleted 2nd or 3rd or .. until the notificationFactor rank, generate ALMOST_TURN notification/timestamp
                sendNotificationAndTimestamp(queue, 0, null, Reason.ALMOST_TURN.getValue(), localDateTime);
            }
        }
    }

    public boolean sendNotificationAndTimestamp (Queue queue,
                                                 int clientIndex,
                                                 Client client,
                                                 String reason,
                                                 LocalDateTime localDateTime){

        boolean notify = false;
        boolean stamp = false;

        if (reason.equals(Reason.TURN.getValue())){
            notify = sendTurnNotification(queue);
            stamp = generateTimeStamp(queue.getQueueId(), queue.getServiceId(), clientIndex, client, reason, localDateTime);
        }
        else if (reason.equals(Reason.ALMOST_TURN.getValue())){
            notify = sendAlmostTurnNotification(queue);
            stamp = true;
        }
        else if (reason.equals(Reason.ADDED.getValue())){
            notify = sendAddedNotification(queue, client);
            stamp = generateTimeStamp(queue.getQueueId(), queue.getServiceId(), clientIndex, client, reason, localDateTime);
        }
        else if (reason.equals(Reason.LATE.getValue())){
            notify = sendLateNotification(queue, clientIndex, client);
            stamp = generateTimeStamp(queue.getQueueId(), queue.getServiceId(), clientIndex, client, reason, localDateTime);
        }
        else if (reason.equals(Reason.DONE.getValue())){
            notify = generateTimeStamp(queue.getQueueId(), queue.getServiceId(), clientIndex, client, reason, localDateTime);
            stamp = true;
        }
        else if (reason.equals(Reason.CANCELLED.getValue())){
            generateTimeStamp(queue.getQueueId(), queue.getServiceId(), clientIndex, client, reason, localDateTime);
        }

        return (notify && stamp);
    }

    public boolean sendTurnNotification(Queue queue) {
        boolean sent = false;
        boolean send;

        if (queue.getClientQueue().size() != 0) {
            Notification notification = new Notification();
            send = notification.setContactInfo(queue.getClientQueue().get(0).getPhoneNumber(),
                    queue.getClientQueue().get(0).getEmailAddress());
            notification.setSubject(queue.getServiceName()+" - "+queue.getQueueName());
            notification.setMsgContent("It's your turn, please present yourself at the reception.");

            if (send){
                rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
                rabbitMessagingTemplate.convertAndSend(
                        Objects.requireNonNull(env.getProperty("rabbitmq.exchange.notification")),
                        Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.turn")),
                        notification);
                sent = true;
            }
        }

        return sent;
    }

    public boolean sendAlmostTurnNotification(Queue queue) {
        boolean sent = false;
        boolean send;

        int notificationFactor = queue.getNotificationFactor();
        if (notificationFactor < queue.getClientQueue().size()) {
            Notification notification = new Notification();
            send = notification.setContactInfo(queue.getClientQueue().get(notificationFactor).getPhoneNumber(),
                    queue.getClientQueue().get(notificationFactor).getEmailAddress());
            notification.setSubject(queue.getServiceName()+" - "+queue.getQueueName());
            notification.setMsgContent(notificationFactor + " Clients left before you!");

            if (send) {
                rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
                rabbitMessagingTemplate.convertAndSend(
                        Objects.requireNonNull(env.getProperty("rabbitmq.exchange.notification")),
                        Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.turn")),
                        notification);
                sent = true;
            }
        }

        return sent;
    }

    public boolean sendLateNotification(Queue queue, int clientIndex, Client client) {
        boolean sent = false;
        boolean send;

        if (client != null && clientIndex == 0){
            Notification notification = new Notification();
            send = notification.setContactInfo(client.getPhoneNumber(), client.getEmailAddress());
            notification.setSubject(queue.getServiceName()+" - "+queue.getQueueName());
            notification.setMsgContent("Sorry, you've been removed for being late.");

            if (send) {
                rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
                rabbitMessagingTemplate.convertAndSend(
                        Objects.requireNonNull(env.getProperty("rabbitmq.exchange.notification")),
                        Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.late")),
                        notification);
                sent = true;
            }
        }

        return sent;
    }

    public boolean sendAddedNotification(Queue queue, Client client) {
        boolean sent = false;
        boolean send;

        if (client != null){
            Notification notification = new Notification();
            int notificationFactor = queue.getNotificationFactor();
            send = notification.setContactInfo(client.getPhoneNumber(), client.getEmailAddress());
            notification.setSubject(queue.getServiceName()+" - "+ queue.getQueueName());
            notification.setMsgContent(String.format("Clients before you: %d.\n"+
                            "Your number: %d.\n" +
                            "You will be notified when there are %d clients before you, " +
                            "and when it's your turn.",
                    queue.getClientQueue().indexOf(client),
                    client.getQueueNumber(),
                    notificationFactor));

            if (send){
                rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
                rabbitMessagingTemplate.convertAndSend(
                        Objects.requireNonNull(env.getProperty("rabbitmq.exchange.notification")),
                        Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.added")),
                        notification);
                sent = true;
            }
        }

        return sent;
    }



    public boolean generateTimeStamp(String queueId,
                                  String serviceId,
                                  int clientIndex,
                                  Client client,
                                  String operationType,
                                  LocalDateTime localDateTime) {

        boolean generated = false;

        if (client != null){
            if (operationType.equals(Reason.ADDED.getValue())
                    || operationType.equals(Reason.CANCELLED.getValue())
                    || (clientIndex == 0
                            && (operationType.equals(Reason.DONE.getValue())
                                    || operationType.equals(Reason.LATE.getValue()))
                                    || operationType.equals(Reason.TURN.getValue()))){

                TimeStamp timeStamp = new TimeStamp(queueId,
                        serviceId,
                        client,
                        localDateTime.getYear(),
                        localDateTime.getMonthValue(),
                        localDateTime.getDayOfMonth(),
                        localDateTime.getHour(),
                        localDateTime.getMinute(),
                        localDateTime.getSecond(),
                        localDateTime.getNano(),
                        operationType);
                rabbitMessagingTemplate.setMessageConverter(this.mappingJackson2MessageConverter);
                rabbitMessagingTemplate.convertAndSend(
                        Objects.requireNonNull(env.getProperty("rabbitmq.exchange.client")),
                        Objects.requireNonNull(env.getProperty("rabbitmq.routingkey.timestamp")),
                        timeStamp);
                generated = true;
            }
        }

        return generated;
    }



}
