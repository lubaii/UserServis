package com.notificationservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Test
    void testSendUserCreatedEmail() {
        // Given
        String recipientEmail = "user@example.com";

        // When
        emailNotificationService.sendUserCreatedEmail(recipientEmail);

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("Создание аккаунта", sentMessage.getSubject());
        assertEquals(recipientEmail, sentMessage.getTo()[0]);
        assertTrue(sentMessage.getText().contains("Ваш аккаунт на сайте ваш сайт был успешно создан."));
    }

    @Test
    void testSendUserDeletedEmail() {
        // Given
        String recipientEmail = "user@example.com";

        // When
        emailNotificationService.sendUserDeletedEmail(recipientEmail);

        // Then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("Удаление аккаунта", sentMessage.getSubject());
        assertEquals(recipientEmail, sentMessage.getTo()[0]);
        assertTrue(sentMessage.getText().contains("Ваш аккаунт был удалён."));
    }

    @Test
    void testSendUserCreatedEmail_ExceptionHandling() {
        // Given
        String recipientEmail = "user@example.com";
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            emailNotificationService.sendUserCreatedEmail(recipientEmail);
        });

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}

