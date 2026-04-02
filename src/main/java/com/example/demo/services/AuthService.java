package com.example.demo.services;

import com.example.demo.DTO.JwtResponse;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.UserDTO.RegisterRequest;
import com.example.demo.configurations.JwtTokenProvider;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.demo.entity.enums.UserRole.USER;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public JwtResponse register(RegisterRequest request) {
        // Проверяем, нет ли уже такого пользователя
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("This email already exists");
        }

        // Создаем нового пользователя
        User user = new User();
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setUserRole(USER);

        userRepository.save(user);

        return login(new LoginRequest(request.getEmail(), request.getPassword()));
    }

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtTokenProvider.createToken(
                request.getEmail(),
                authentication.getAuthorities()
        );

        return new JwtResponse(token, request.getEmail());
    }
}
