package com.notificationservice.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.notificationservice.config.TestMailConfig;
import com.notificationservice.event.UserEvent;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.mail.host=localhost",
    "spring.mail.port=3025",
    "spring.mail.username=test@test.com",
    "spring.mail.password=test",
    "spring.mail.properties.mail.smtp.auth=false",
    "spring.mail.properties.mail.smtp.starttls.enable=false",
    "spring.kafka.bootstrap-servers=localhost:9092",
    "kafka.topic.user-events=test-user-events"
})
@Import(TestMailConfig.class)
class UserEventListenerIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@test.com", "test"))
            .withPerMethodLifecycle(false);

    @Autowired
    private UserEventListener userEventListener;

    @BeforeEach
    void setUp() {
        // Очистка полученных сообщений перед каждым тестом
        greenMail.reset();
    }

    @Test
    void testHandleUserEvent_CreateOperation() throws MessagingException, java.io.IOException {
        // Given
        UserEvent event = new UserEvent();
        event.setOperation("CREATE");
        event.setEmail("user@example.com");

        ConsumerRecord<String, UserEvent> record = new ConsumerRecord<>(
            "test-user-events", 0, 0, "user@example.com", event
        );

        // When
        userEventListener.handleUserEvent(record);

        // Then
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Должно быть отправлено одно письмо");

        MimeMessage message = messages[0];
        assertEquals("Создание аккаунта", message.getSubject());
        assertEquals("user@example.com", message.getRecipients(javax.mail.Message.RecipientType.TO)[0].toString());
        String content = message.getContent().toString();
        assertTrue(content.contains("Ваш аккаунт на сайте ваш сайт был успешно создан."),
            "Содержимое письма должно содержать текст о создании аккаунта");
    }

    @Test
    void testHandleUserEvent_DeleteOperation() throws MessagingException, java.io.IOException {
        // Given
        UserEvent event = new UserEvent();
        event.setOperation("DELETE");
        event.setEmail("user@example.com");

        ConsumerRecord<String, UserEvent> record = new ConsumerRecord<>(
            "test-user-events", 0, 0, "user@example.com", event
        );

        // When
        userEventListener.handleUserEvent(record);

        // Then
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Должно быть отправлено одно письмо");

        MimeMessage message = messages[0];
        assertEquals("Удаление аккаунта", message.getSubject());
        assertEquals("user@example.com", message.getRecipients(javax.mail.Message.RecipientType.TO)[0].toString());
        String content = message.getContent().toString();
        assertTrue(content.contains("Ваш аккаунт был удалён."),
            "Содержимое письма должно содержать текст об удалении аккаунта");
    }

    @Test
    void testHandleUserEvent_InvalidEvent() {
        // Given
        UserEvent event = new UserEvent();
        event.setOperation("CREATE");
        event.setEmail(null); // Некорректный email

        ConsumerRecord<String, UserEvent> record = new ConsumerRecord<>(
            "test-user-events", 0, 0, null, event
        );

        // When
        userEventListener.handleUserEvent(record);

        // Then - письмо не должно быть отправлено
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(0, messages.length, "Письмо не должно быть отправлено для некорректного события");
    }

    @Test
    void testHandleUserEvent_UnknownOperation() {
        // Given
        UserEvent event = new UserEvent();
        event.setOperation("UNKNOWN");
        event.setEmail("user@example.com");

        ConsumerRecord<String, UserEvent> record = new ConsumerRecord<>(
            "test-user-events", 0, 0, "user@example.com", event
        );

        // When
        userEventListener.handleUserEvent(record);

        // Then - письмо не должно быть отправлено для неизвестной операции
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(0, messages.length, "Письмо не должно быть отправлено для неизвестной операции");
    }

    @Test
    void testHandleUserEvent_NullEvent() {
        // Given
        ConsumerRecord<String, UserEvent> record = new ConsumerRecord<>(
            "test-user-events", 0, 0, null, null
        );

        // When
        userEventListener.handleUserEvent(record);

        // Then - письмо не должно быть отправлено
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(0, messages.length, "Письмо не должно быть отправлено для null события");
    }
}

