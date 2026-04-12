package com.example.demo.DTO.AdminDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;


@Data
public class NotificationCreateAllUsersDto {
    @NotBlank
    private String message;

    private List<String> emailsUsers;
}
