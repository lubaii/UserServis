package com.notificationservice.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.notificationservice.config.TestMailConfig;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({TestMailConfig.class, EmailNotificationServiceIntegrationTest.TestMailConfiguration.class})
class EmailNotificationServiceIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@test.com", "test"))
            .withPerMethodLifecycle(false);

    @Autowired
    private EmailNotificationService emailNotificationService;

    @BeforeEach
    void setUp() {
        greenMail.reset();
    }

    @TestConfiguration
    static class TestMailConfiguration {
        @Bean
        @Primary
        public JavaMailSender javaMailSender() {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("localhost");
            mailSender.setPort(3025);
            mailSender.setUsername("test@test.com");
            mailSender.setPassword("test");
            
            Properties props = new Properties();
            props.put("mail.smtp.auth", "false");
            props.put("mail.smtp.starttls.enable", "false");
            props.put("mail.smtp.ssl.enable", "false");
            mailSender.setJavaMailProperties(props);
            
            return mailSender;
        }
    }

    @Test
    void testSendUserCreatedEmail() throws MessagingException, java.io.IOException {
        // Given
        String recipientEmail = "user@example.com";

        // When
        emailNotificationService.sendUserCreatedEmail(recipientEmail);

        // Then
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Должно быть отправлено одно письмо");

        MimeMessage message = messages[0];
        assertEquals("Создание аккаунта", message.getSubject());
        assertEquals(recipientEmail, message.getRecipients(javax.mail.Message.RecipientType.TO)[0].toString());
        String content = message.getContent().toString();
        assertTrue(content.contains("Ваш аккаунт на сайте ваш сайт был успешно создан."),
            "Содержимое письма должно содержать текст о создании аккаунта");
    }

    @Test
    void testSendUserDeletedEmail() throws MessagingException, java.io.IOException {
        // Given
        String recipientEmail = "user@example.com";

        // When
        emailNotificationService.sendUserDeletedEmail(recipientEmail);

        // Then
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Должно быть отправлено одно письмо");

        MimeMessage message = messages[0];
        assertEquals("Удаление аккаунта", message.getSubject());
        assertEquals(recipientEmail, message.getRecipients(javax.mail.Message.RecipientType.TO)[0].toString());
        String content = message.getContent().toString();
        assertTrue(content.contains("Ваш аккаунт был удалён."),
            "Содержимое письма должно содержать текст об удалении аккаунта");
    }

    @Test
    void testSendMultipleEmails() throws MessagingException, java.io.IOException {
        // Given
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        // When
        emailNotificationService.sendUserCreatedEmail(email1);
        emailNotificationService.sendUserDeletedEmail(email2);

        // Then
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(2, messages.length, "Должно быть отправлено два письма");

        // Проверка первого письма (CREATE)
        MimeMessage message1 = messages[0];
        assertEquals("Создание аккаунта", message1.getSubject());
        assertEquals(email1, message1.getRecipients(javax.mail.Message.RecipientType.TO)[0].toString());

        // Проверка второго письма (DELETE)
        MimeMessage message2 = messages[1];
        assertEquals("Удаление аккаунта", message2.getSubject());
        assertEquals(email2, message2.getRecipients(javax.mail.Message.RecipientType.TO)[0].toString());
    }
}

