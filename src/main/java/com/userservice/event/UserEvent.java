package com.userservice.event;

import java.time.LocalDateTime;

/**
 * Событие для отправки в Kafka при создании или удалении пользователя.
 */
public class UserEvent {
    private String operation; // "CREATE" или "DELETE"
    private String email;
    private LocalDateTime timestamp;

    public UserEvent() {
    }

    public UserEvent(String operation, String email) {
        this.operation = operation;
        this.email = email;
        this.timestamp = LocalDateTime.now();
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "operation='" + operation + '\'' +
                ", email='" + email + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

