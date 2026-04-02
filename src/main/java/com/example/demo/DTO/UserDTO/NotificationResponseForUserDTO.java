package com.example.demo.DTO.UserDTO;


import com.example.demo.entity.MessageStatuses;
import lombok.Data;

@Data
public class NotificationResponseForUserDTO {
    private String message;
    private String status;
    private String anotherUserEmail;

    public NotificationResponseForUserDTO(MessageStatuses notification) {
        this.message = notification.getNotification().getMessage();
        this.status = notification.getStatus();
        this.anotherUserEmail = notification.getUser().getEmail() != null
                ? notification.getUser().getEmail() : null;
    }
}
