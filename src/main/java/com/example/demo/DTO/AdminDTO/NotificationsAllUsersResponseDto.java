package com.example.demo.DTO.AdminDTO;

import lombok.Data;

@Data
public class NotificationsAllUsersResponseDto {
    private String message;
    private String description;

    public NotificationsAllUsersResponseDto(String message, String description) {
        this.message = message;
        this.description = description;
    }

    public static String generateDescription(int success) {
        return String.format("Сообщение отправлено %d пользователям", success);
    }
}
