package com.example.demo.DTO.UserDTO;


import com.example.demo.entity.User;
import lombok.Data;

@Data
public class ForUserResponse {
    private String name;
    private String email;

    public ForUserResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
