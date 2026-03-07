package com.example.demo.DTO.AdminDTO;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateDTO {

    private String name;

    @Email
    private String email;

    private String password;

    private String userRole;
}
