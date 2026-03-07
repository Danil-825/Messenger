package com.example.demo.DTO.UserDTO;


import com.example.demo.entity.Notification;
import lombok.Data;

@Data
public class NotificationResponseForUserDTO {
    private String title;
    private String content;
    private String status;
    private String anotherUserEmail;

    public NotificationResponseForUserDTO(Notification notification) {
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.status = notification.getStatus();
        this.anotherUserEmail = notification.getAnotherUser() != null ? notification.getAnotherUser().getEmail() : null;
    }
}
