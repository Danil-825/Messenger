package com.example.demo.services;


import com.example.demo.DTO.AdminDTO.UserCreateDto;
import com.example.demo.DTO.AdminDTO.UserResponseDTO;
import com.example.demo.DTO.UserDTO.ForUserResponse;
import com.example.demo.DTO.UserDTO.ForUserUpdateDto;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.exceptions.EmailAlreadyExistsException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Long userId;
    private User existingUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        userId = 1L;
        existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Mihael");
        existingUser.setEmail("mihael@gmail.com");
        existingUser.setPassword("password");
        existingUser.setUserRole(UserRole.USER);
        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Mihael");
        anotherUser.setEmail("mihael@gmail.com");
        anotherUser.setPassword("password");
        anotherUser.setUserRole(UserRole.USER);
    }



    @Test
    void findById_shouldReturnUserResponseDTO_whenUserExists() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // When
        UserResponseDTO result = userService.findById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("Mihael");
        assertThat(result.getEmail()).isEqualTo("mihael@gmail.com");
        assertThat(result.getUserRole()).isEqualTo("USER");

        verify(userRepository).findById(userId);
    }

    @Test
    void findById_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // Given
        Long nonExistentId = 999L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class,
                () -> userService.findById(nonExistentId));

        verify(userRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void findAll_shouldReturnAllUserResponseDTO_whenUsersExists() {

        List<User> userList = Arrays.asList(existingUser, anotherUser);

        when(userRepository.findAll()).thenReturn(userList);

        List<UserResponseDTO> result = userService.findAll();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(userList.size());
        verify(userRepository).findAll();
    }

    @Test
    void findAll_shouldThrowUserNotFoundException_whenUsersDoesNotExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThrows(UserNotFoundException.class,
                () -> userService.findAll());
        verify(userRepository, times(1)).findAll();
    }

    //create
    @Test
    void create_shouldReturnUserResponseDTO_whenDataIsValid() {

        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("Mihael");
        createDto.setEmail("mihael@mail.com");
        createDto.setPassword("mihael123");

        when(userRepository.findByEmail(createDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(createDto.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(createDto.getName());
        savedUser.setEmail(createDto.getEmail());
        savedUser.setPassword("encodedPassword");
        savedUser.setUserRole(UserRole.USER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);


        UserResponseDTO result = userService.create(createDto);


        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Mihael");
        assertThat(result.getEmail()).isEqualTo("mihael@mail.com");
        assertThat(result.getUserRole()).isEqualTo("USER");

        verify(userRepository).findByEmail(createDto.getEmail());
        verify(passwordEncoder).encode(createDto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowEmailAlreadyExistsException_whenEmailAlreadyExists() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setEmail("mihael@mail.com");

        User userExists = new User();
        userExists.setEmail("mihael@mail.com");
        when(userRepository.findByEmail(userExists.getEmail())).thenReturn(Optional.of(userExists));
        assertThatThrownBy(() -> userService.create(createDto))
                .isInstanceOf(EmailAlreadyExistsException.class);
        verify(userRepository, times(1)).findByEmail(createDto.getEmail());
    }

    //Update
    @Test
    void updateUser_shouldUpdateFields_whenUserExistsAndDataIsValid() {
        // GIVEN
        String email = "old@email.com";

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Old Name");
        existingUser.setEmail(email);
        existingUser.setPassword("oldPassword");
        existingUser.setUserRole(UserRole.USER);

        ForUserUpdateDto dto = new ForUserUpdateDto();
        dto.setName("New Name");
        dto.setEmail("new@email.com");
        dto.setPassword("newPassword");

        // Настраиваем моки
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty()); // email свободен
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedNewPassword");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("New Name");
        updatedUser.setEmail("new@email.com");
        updatedUser.setPassword("encodedNewPassword");
        updatedUser.setUserRole(UserRole.USER);

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // WHEN
        ForUserResponse result = userService.updateUser(email, dto);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@email.com");

        verify(userRepository).findByEmail(email);
        verify(userRepository).findByEmail(dto.getEmail()); // проверка свободного email
        verify(passwordEncoder).encode(dto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowException_whenNewEmailAlreadyTaken() {
        // GIVEN
        String oldEmail = "user@email.com";

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail(oldEmail);

        ForUserUpdateDto dto = new ForUserUpdateDto();
        dto.setEmail("taken@email.com");

        User userWithNewEmail = new User();
        userWithNewEmail.setId(2L);
        userWithNewEmail.setEmail("taken@email.com");

        when(userRepository.findByEmail(oldEmail)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(userWithNewEmail)); // email занят

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(oldEmail, dto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("already exists");

        verify(userRepository).findByEmail(oldEmail);
        verify(userRepository).findByEmail(dto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        // GIVEN
        String email = "nonexistent@email.com";
        ForUserUpdateDto dto = new ForUserUpdateDto();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> userService.updateUser(email, dto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteById_shouldCallDeleteById_whenUserExists() {

        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void findByName_shouldCallFindByName_whenUserExists() {

        List<User> userList = Arrays.asList(existingUser, anotherUser);

        when(userRepository.findByName(existingUser.getName())).thenReturn(userList);

        List<ForUserResponse> result = userService.findByName(existingUser.getName());

        assertThat(result).isNotNull();
        verify(userRepository).findByName(existingUser.getName());
    }

    @Test
    void findByName_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByName(existingUser.getName())).thenReturn(List.of());

        assertThatThrownBy(() -> userService.findByName(existingUser.getName()))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByName(existingUser.getName());
    }

    @Test
    void findByEmail_shouldCallFindByEmail_whenUserExists() {
        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        UserResponseDTO result = userService.findByEmail(existingUser.getEmail());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("Mihael");
        assertThat(result.getEmail()).isEqualTo("mihael@gmail.com");
        assertThat(result.getUserRole()).isEqualTo("USER");

        verify(userRepository).findByEmail(existingUser.getEmail());
    }
}
