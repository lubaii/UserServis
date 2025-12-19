package com.notificationservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.notificationservice.dto.EmailRequest;
import com.notificationservice.service.EmailNotificationService;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void testSendEmail_CreateOperation() {
        // Given
        EmailRequest request = new EmailRequest();
        request.setOperation("CREATE");
        request.setEmail("user@example.com");

        // When
        ResponseEntity<Void> response = notificationController.sendEmail(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailNotificationService, times(1)).sendUserCreatedEmail("user@example.com");
    }

    @Test
    void testSendEmail_DeleteOperation() {
        // Given
        EmailRequest request = new EmailRequest();
        request.setOperation("DELETE");
        request.setEmail("user@example.com");

        // When
        ResponseEntity<Void> response = notificationController.sendEmail(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailNotificationService, times(1)).sendUserDeletedEmail("user@example.com");
    }

    @Test
    void testSendEmail_InvalidOperation() {
        // Given
        EmailRequest request = new EmailRequest();
        request.setOperation("INVALID");
        request.setEmail("user@example.com");

        // When
        ResponseEntity<Void> response = notificationController.sendEmail(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(emailNotificationService, never()).sendUserCreatedEmail(anyString());
        verify(emailNotificationService, never()).sendUserDeletedEmail(anyString());
    }

    @Test
    void testSendEmail_MissingEmail() {
        // Given
        EmailRequest request = new EmailRequest();
        request.setOperation("CREATE");
        request.setEmail(null);

        // When
        ResponseEntity<Void> response = notificationController.sendEmail(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(emailNotificationService, never()).sendUserCreatedEmail(anyString());
    }

    @Test
    void testSendEmail_EmptyEmail() {
        // Given
        EmailRequest request = new EmailRequest();
        request.setOperation("CREATE");
        request.setEmail("");

        // When
        ResponseEntity<Void> response = notificationController.sendEmail(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(emailNotificationService, never()).sendUserCreatedEmail(anyString());
    }
}

