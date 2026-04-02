package com.example.demo.DTO.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ChatCreateDtoForUser {
    @NotBlank
    private String title;
    @NotEmpty
    private List<String> emails;
}
