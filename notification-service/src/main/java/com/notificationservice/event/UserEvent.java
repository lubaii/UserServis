package com.notificationservice.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEvent {

    private String operation;
    private String email;
    
    @JsonIgnore // Игнорируем timestamp при десериализации, он не нужен для отправки писем
    private LocalDateTime timestamp;

    public UserEvent() {
        // default constructor for deserialization
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


