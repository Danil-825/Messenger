package com.example.demo.controllers;

import com.example.demo.DTO.UserDTO.NotificationCreateDTO;
import com.example.demo.DTO.AdminDTO.NotificationResponseDTO;
import com.example.demo.DTO.UserDTO.NotificationResponseForUserDTO;
import com.example.demo.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @Operation(summary="Найти сообщение по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение найден"),
            @ApiResponse(responseCode = "404", description = "Сообщение не найдено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/notif/id/{id}")
    public NotificationResponseDTO findById(@PathVariable Long id) {
        return notificationService.findById(id);
    }


    @Operation(summary="Вывести сообщения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщения найдены"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/notif/all")
    public List<NotificationResponseDTO> findAll() {
        return notificationService.findAll();
    }


    @Operation(summary="Найти сообщение по title")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение найдено"),
            @ApiResponse(responseCode = "404", description = "Сообщение не найдено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/notif/title/{title}")
    public List<NotificationResponseDTO> findByTitle(@PathVariable String title) {
        return notificationService.findByTitle(title);
    }


    @Operation(summary="Найти сообщение по userId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение найдено"),
            @ApiResponse(responseCode = "404", description = "Сообщение не найдено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/notif/userid/{userId}")
    public List<NotificationResponseDTO> findByUserId(@PathVariable Long userId) {
        return notificationService.findByUserId(userId);
    }


    @Operation(summary="Найти сообщения по email юзера")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщения найдены"),
            @ApiResponse(responseCode = "404", description = "Сообщения не найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/notif/email/{email}")
    public List<NotificationResponseForUserDTO> findByUserEmail(@Valid @PathVariable String email) {
        return notificationService.findByUserEmail(email);
    }

    @Operation(summary = "Создать сообщение", description = "Регистрирует новое сообщение")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Сообщение создано"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
    })
    @PostMapping("/user/notif/create")
    public NotificationResponseForUserDTO create (@AuthenticationPrincipal UserDetails user, @Valid @RequestBody NotificationCreateDTO notificationCreateDTO) {
        return notificationService.create(user.getUsername(), notificationCreateDTO);
    }

    @Operation(summary="Удаление сообщения", description = "Удаляет сообщение")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/notif/del/{id}")
    public void delete (@PathVariable Long id) {
        notificationService.delete(id);
    }


    @Operation(summary="поиск всех сообщений юзера",
            description = "ищет сообщения авторизованного юзера")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщения найдены"),
            @ApiResponse(responseCode = "404", description = "Сообщения не найдены"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/mynotif/all")
    public List<NotificationResponseForUserDTO> findAllForUser (
            @AuthenticationPrincipal UserDetails currentUser) {
        return notificationService.findByUserEmail(currentUser.getUsername());
    }


    @Operation(summary="Ищет сообщение пользователя по названию",
            description = "Ищет сообщение по названию авторизованного пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщения найдены"),
            @ApiResponse(responseCode = "404", description = "Сообщения не найдены"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/mynotif/{title}")
    public List<NotificationResponseForUserDTO> findByTitleAndUserEmail (@PathVariable String title,
                @AuthenticationPrincipal UserDetails currentUser) {
        return notificationService.findByTitleAndUserEmail(title, currentUser.getUsername());
    }
}
