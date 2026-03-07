package com.example.demo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String type="Bearer";
    private String userName;

    public JwtResponse(String token, @NotBlank @Email String email) {
        this.token = token;
        this.userName = email;
    }
}
