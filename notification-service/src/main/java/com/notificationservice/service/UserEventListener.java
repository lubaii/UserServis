package com.notificationservice.service;

import com.notificationservice.event.UserEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventListener {

    private static final Logger logger = LogManager.getLogger(UserEventListener.class);

    private final EmailNotificationService emailNotificationService;

    public UserEventListener(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @KafkaListener(topics = "${kafka.topic.user-events}", groupId = "notification-service")
    public void handleUserEvent(ConsumerRecord<String, UserEvent> record) {
        try {
            UserEvent event = record.value();

            if (event == null || event.getEmail() == null) {
                logger.warn("Received invalid user event: {}", event);
                return;
            }

            logger.info("Received user event from Kafka: operation={}, email={}", 
                event.getOperation(), event.getEmail());

            switch (event.getOperation()) {
                case "CREATE" -> {
                    logger.info("Processing CREATE event for email: {}", event.getEmail());
                    emailNotificationService.sendUserCreatedEmail(event.getEmail());
                }
                case "DELETE" -> {
                    logger.info("Processing DELETE event for email: {}", event.getEmail());
                    emailNotificationService.sendUserDeletedEmail(event.getEmail());
                }
                default -> logger.warn("Unknown operation in user event: {}", event.getOperation());
            }
        } catch (Exception e) {
            logger.error("Error processing user event from Kafka. Record: {}", record, e);
            throw e; // Пробрасываем дальше для retry механизма Kafka
        }
    }
}


