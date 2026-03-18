package com.example.demo.DTO.UserDTO;


import com.example.demo.entity.Notification;
import lombok.Data;

@Data
public class NotificationResponseForUserDTO {
    private String message;
    private String status;
    private String anotherUserEmail;

    public NotificationResponseForUserDTO(Notification notification) {
        this.message = notification.getMessage();
        this.status = notification.getStatus();
        this.anotherUserEmail = notification.getAnotherUser() != null ? notification.getAnotherUser().getEmail() : null;
    }
}
