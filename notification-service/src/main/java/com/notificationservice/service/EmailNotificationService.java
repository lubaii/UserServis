package com.notificationservice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger logger = LogManager.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendUserCreatedEmail(String to) {
        try {
            logger.info("Sending user created email to: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("lubachivi@yandex.ru");
            message.setTo(to);
            message.setSubject("Создание аккаунта");
            message.setText("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.");
            mailSender.send(message);
            logger.info("User created email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send user created email to: {}", to, e);
            throw e;
        }
    }

    public void sendUserDeletedEmail(String to) {
        try {
            logger.info("Sending user deleted email to: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("lubachivi@yandex.ru");
            message.setTo(to);
            message.setSubject("Удаление аккаунта");
            message.setText("Здравствуйте! Ваш аккаунт был удалён.");
            mailSender.send(message);
            logger.info("User deleted email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send user deleted email to: {}", to, e);
            throw e;
        }
    }
}


