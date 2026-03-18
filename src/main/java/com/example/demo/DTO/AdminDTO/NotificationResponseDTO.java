package com.example.demo.DTO.AdminDTO;

import com.example.demo.entity.Notification;
import lombok.Data;

@Data
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private Long userId;
    private String status;
    private Long anotherUserId;

    public NotificationResponseDTO(Notification notification) {
        this.id = notification.getId();
        this.message = notification.getMessage();
        this.status = notification.getStatus();
        this.userId = notification.getUser() != null ? notification.getUser().getId() : null;
        this.anotherUserId = notification.getAnotherUser() != null ? notification.getAnotherUser().getId() : null;
    }

}
