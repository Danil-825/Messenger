package com.example.demo.services;

import com.example.demo.DTO.AdminDTO.UserCreateDto;
import com.example.demo.DTO.AdminDTO.UserResponseDTO;
import com.example.demo.DTO.AdminDTO.UserUpdateDTO;
import com.example.demo.DTO.UserDTO.ForUserResponse;
import com.example.demo.DTO.UserDTO.ForUserUpdateDto;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    public UserResponseDTO findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        User userFound = user.orElseThrow(() -> new RuntimeException("Not found"));
        return new UserResponseDTO(userFound);
    }

    public UserResponseDTO create(UserCreateDto createDto) {
        User user = new User();
        user.setName(createDto.getName());
        user.setEmail(createDto.getEmail());
        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        user.setUserRole(UserRole.valueOf("USER"));
        User saved = userRepository.save(user);
        return new UserResponseDTO(saved);
    }

    private User updateBaseFields(User user, String name, String email, String password) {
        if (name != null) user.setName(name);
        if (email != null) user.setEmail(email);
        if (password != null) user.setPassword(passwordEncoder.encode(password));
        return user;
    }

    public ForUserResponse updateUser(String email, ForUserUpdateDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not found"));

        user = updateBaseFields(user, dto.getName(), dto.getEmail(), dto.getPassword());
        User saved = userRepository.save(user);

        return new ForUserResponse(saved);
    }

    public UserResponseDTO update(String email, UserUpdateDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not found"));

        user = updateBaseFields(user, dto.getName(), dto.getEmail(), dto.getPassword());

        if (dto.getUserRole() != null) {
            user.setUserRole(UserRole.valueOf(dto.getUserRole()));
        }

        User saved = userRepository.save(user);
        return new UserResponseDTO(saved);
    }

    public List<ForUserResponse> findByName(String name) {
        List<User> users = userRepository.findByName(name);
        return users.stream()
                .map(ForUserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponseDTO findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        User userFound = user.orElseThrow(() -> new RuntimeException("Not found"));
        return new UserResponseDTO(userFound);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public ForUserResponse findForUserByEmail(String email) {
        Optional <User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new RuntimeException("Not found"));
        return new ForUserResponse(user);
    }

    public List<ForUserResponse> findAllUsers() {
        List<User> users = userRepository.findAllUsers();
        return users.stream()
                .map(ForUserResponse::new)
                .collect(Collectors.toList());
    }



}

