package com.example.demo.DTO.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationCreateInChatForUserDto {
    @NotNull
    private Long chatId;
    @NotBlank
    private String message;
}
