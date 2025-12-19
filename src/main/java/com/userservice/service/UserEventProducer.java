package com.userservice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.userservice.event.UserEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Сервис для отправки событий пользователя в Kafka.
 */
@Service
public class UserEventProducer {
    private static final Logger logger = LogManager.getLogger(UserEventProducer.class);
    
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    
    @Value("${kafka.topic.user-events}")
    private String topicName;

    @Autowired
    public UserEventProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Отправляет событие создания пользователя в Kafka.
     *
     * @param email email пользователя
     */
    public void sendUserCreatedEvent(String email) {
        UserEvent event = new UserEvent("CREATE", email);
        sendEvent(event);
    }

    /**
     * Отправляет событие удаления пользователя в Kafka.
     *
     * @param email email пользователя
     */
    public void sendUserDeletedEvent(String email) {
        UserEvent event = new UserEvent("DELETE", email);
        sendEvent(event);
    }

    private void sendEvent(UserEvent event) {
        try {
            CompletableFuture<SendResult<String, UserEvent>> future = 
                kafkaTemplate.send(topicName, event.getEmail(), event);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("User event sent successfully: {} to topic: {}", 
                        event, topicName);
                } else {
                    logger.error("Failed to send user event: {} to topic: {}", 
                        event, topicName, exception);
                }
            });
        } catch (Exception e) {
            logger.error("Error sending user event: {} to topic: {}", 
                event, topicName, e);
        }
    }
}

