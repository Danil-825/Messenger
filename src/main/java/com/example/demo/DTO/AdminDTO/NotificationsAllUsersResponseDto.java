package com.example.demo.DTO.AdminDTO;

import com.example.demo.entity.MessageStatuses;
import lombok.Data;

@Data
public class NotificationsAllUsersResponseDto {
    private String message;
    private String description;

    public NotificationsAllUsersResponseDto(String message, String description) {
        this.message = message;
        this.description = description;
    }

    public NotificationsAllUsersResponseDto(MessageStatuses notification) {
        this.message = notification.getNotification().getMessage();
    }

    public static String generateDescription(int success) {
        return String.format("Сообщение отправлено %d пользователям", success);
    }
}
