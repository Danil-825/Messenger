package com.example.demo.DTO.UserDTO;

import com.example.demo.entity.Notification;
import lombok.Data;

@Data
public class ResponseToNotificationCreationForUser {
    private String message;
    private String titleChat;

    public ResponseToNotificationCreationForUser(Notification notification) {
        this.message = notification.getMessage();
        this.titleChat = notification.getChat().getTitle() != null
                ? notification.getChat().getTitle() : null;
    }
}
