package com.example.demo.DTO.UserDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationCreateDTO {
    @NotBlank
    private String message;
    @NotBlank
    private String anotherEmailUser;
}
