package com.example.demo.services;

import com.example.demo.DTO.AdminDTO.UserCreateDto;
import com.example.demo.DTO.AdminDTO.UserResponseDTO;
import com.example.demo.DTO.AdminDTO.UserUpdateDTO;
import com.example.demo.DTO.UserDTO.ForUserResponse;
import com.example.demo.DTO.UserDTO.ForUserUpdateDto;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.exceptions.EmailAlreadyExistsException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        List<User> users = userRepository.findAll();
        checkList(users);
        return users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    public UserResponseDTO findById(Long id) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findById(id), "id", String.valueOf(id)
        );
        return new UserResponseDTO(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO create(UserCreateDto createDto) {
        User user = new User();
        user.setName(createDto.getName());
        user.setEmail(createDto.getEmail());
        throwIfEmailExists(() -> userRepository.findByEmail(createDto.getEmail()), createDto.getEmail());
        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        user.setUserRole(UserRole.valueOf("USER"));
        User saved = userRepository.save(user);
        return new UserResponseDTO(saved);
    }

    private User updateBaseFields(User user, String name, String email, String password) {
        if (name != null) user.setName(name);
        if (email != null) {
            user.setEmail(email);
            throwIfEmailExists(() -> userRepository.findByEmail(email), email);
        }
        if (password != null) user.setPassword(passwordEncoder.encode(password));
        return user;
    }


    @Transactional(rollbackFor = Exception.class)
    public ForUserResponse updateUser(String email, ForUserUpdateDto dto) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByEmail(email), "email", email
        );

        User userUpdate = updateBaseFields(user, dto.getName(), dto.getEmail(), dto.getPassword());
        User saved = userRepository.save(userUpdate);

        return new ForUserResponse(saved);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO updateForAdmin(String email, UserUpdateDTO dto) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByEmail(email), "email", email
        );

        User userUpdate = updateBaseFields(user, dto.getName(), dto.getEmail(), dto.getPassword());

        if (dto.getUserRole() != null) {
            user.setUserRole(UserRole.valueOf(dto.getUserRole()));
        }

        User saved = userRepository.save(userUpdate);
        return new UserResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ForUserResponse> findByName(String name) {
        List<User> users = userRepository.findByName(name);
        checkList(users);
        return users.stream()
                .map(ForUserResponse::new)
                .collect(Collectors.toList());
    }


    public UserResponseDTO findByEmail(String email) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByEmail(email), "email", email
        );
        return new UserResponseDTO(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public ForUserResponse findForUserByEmail(String email) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByEmail(email),
                "email", email
        );
        return new ForUserResponse(user);
    }

    @Transactional(readOnly = true)
    public List<ForUserResponse> findAllUsers() {
        List<User> users = userRepository.findAllUsers();
        checkList(users);
        return users.stream()
                .map(ForUserResponse::new)
                .collect(Collectors.toList());
    }




    private User findByThrowUserNotFound(Supplier<Optional<User>> supplier, String field, String value) {
        return supplier.get()
                .orElseThrow(() -> {
                    log.warn("User not found with {}: {}", field, value);
                    return new UserNotFoundException("User with " + field + " " + value + " not found");
                });
    }

    private void throwIfEmailExists(Supplier<Optional<User>> supplier, String value) {
        supplier.get().ifPresent(user -> {
            log.warn("Email already exists: {}", value);
            throw new EmailAlreadyExistsException("Email already exists: " + value);
        });
    }

    private void checkList(List<User> users) {
        if (users.isEmpty()){
            log.warn("Users not found");
            throw new UserNotFoundException("Users not found");
        }
    }
}