package com.example.demo.DTO.AdminDTO;

import com.example.demo.entity.MessageStatuses;
import lombok.Data;

@Data
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private Long userId;
    private String status;
    private Long anotherUserId;

    public NotificationResponseDTO(MessageStatuses notification) {
        this.id = notification.getNotification().getId();
        this.message = notification.getNotification().getMessage();
        this.status = notification.getStatus();
        this.userId = notification.getNotification().getUser().getId() != null
                ? notification.getNotification().getUser().getId() : null;
        this.anotherUserId = notification.getUser().getId() != null
                ? notification.getUser().getId() : null;
    }

}
