package com.example.demo.DTO.UserDTO;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ForUserUpdateDto {

    private String name;

    @Email
    private String email;

    private String password;
}
