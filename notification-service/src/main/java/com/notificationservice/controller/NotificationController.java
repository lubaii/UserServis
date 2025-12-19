package com.notificationservice.controller;

import com.notificationservice.dto.EmailRequest;
import com.notificationservice.service.EmailNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailNotificationService emailNotificationService;

    public NotificationController(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendEmail(@RequestBody EmailRequest request) {
        String operation = request.getOperation();
        String email = request.getEmail();

        if (email == null || email.isBlank() || operation == null || operation.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        switch (operation) {
            case "CREATE" -> emailNotificationService.sendUserCreatedEmail(email);
            case "DELETE" -> emailNotificationService.sendUserDeletedEmail(email);
            default -> {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok().build();
    }
}


