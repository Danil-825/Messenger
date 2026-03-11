package com.example.demo.controllers;

import com.example.demo.DTO.AdminDTO.UserCreateDto;
import com.example.demo.DTO.AdminDTO.UserResponseDTO;
import com.example.demo.DTO.AdminDTO.UserUpdateDTO;
import com.example.demo.DTO.UserDTO.ForUserResponse;
import com.example.demo.DTO.UserDTO.ForUserUpdateDto;
import com.example.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Пользователи", description = "Операции с пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "Найти пользователей по name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/name/{name}")
    public List<ForUserResponse> findByName(@PathVariable String name) {
        return userService.findByName(name);
    }


    @Operation(summary = "Найти пользователя по email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/email/{email}")
    public UserResponseDTO findByEmail(@Valid @PathVariable String email) {
        return userService.findByEmail(email);
    }

    @Operation(summary = "Все пользователи")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public List<UserResponseDTO> findAll() {
        return userService.findAll();
    }

    @Operation(summary = "Найти пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/id/{id}")
    public UserResponseDTO findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(summary = "Создать пользователя", description = "Регистрирует нового пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "409", description = "такой email уже есть")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/create")
    public UserResponseDTO create(@Valid @RequestBody UserCreateDto user) {
        return userService.create(user);
    }

    @Operation(summary = "Обновить данные пользователя", description = "Обновляет данные пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "этот email уже есть)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update/{email}")
    public UserResponseDTO updateForAdmin(@Valid @PathVariable String email,
                                     @Valid @RequestBody UserUpdateDTO user) {
        return userService.updateForAdmin(email, user);
    }

    @Operation(summary = "Удаление пользователя", description = "Удаляет пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/del/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }


    @Operation(summary = "Обновление пользователя им самостоятельно", description = "Пользователь сам обновляет свои данные")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "этот email уже есть)")
    })
    @PutMapping("/user/update/{email}")
    public ForUserResponse updateUser(@AuthenticationPrincipal UserDetails email, @Valid @RequestBody ForUserUpdateDto user) {
        return userService.updateUser(email.getUsername(), user);
    }


    @Operation(summary = "Поиск пользователя пользователем", description = "Пользователь ищет другого пользователя по email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/findUser/{email}")
    public ForUserResponse findForUserByEmail(@Valid @PathVariable String email) {
        return userService.findForUserByEmail(email);
    }

    @Operation(summary = "Вывод всех пользователей для пользователя", description = "Пользователь смотрит на всех других пользователей")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/all")
    public List<ForUserResponse> findAllUsers() {
        return userService.findAllUsers();
    }



}