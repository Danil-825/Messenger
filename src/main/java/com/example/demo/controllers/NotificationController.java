package com.example.demo.controllers;

import com.example.demo.DTO.UserDTO.NotificationCreateDTO;
import com.example.demo.DTO.AdminDTO.NotificationResponseDTO;
import com.example.demo.DTO.UserDTO.NotificationCreateInChatForUserDto;
import com.example.demo.DTO.UserDTO.NotificationResponseForUserDTO;
import com.example.demo.DTO.UserDTO.ResponseToNotificationCreationForUser;
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
    public List<NotificationResponseDTO> findById(@PathVariable Long id) {
        return notificationService.findById(id);
    }

    @Operation(summary="Найти сообщение по message")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение найдено"),
            @ApiResponse(responseCode = "404", description = "Сообщение не найдено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/notif/message/{message}")
    public List<NotificationResponseDTO> findByMessage(@PathVariable String message) {
        return notificationService.findByMessage(message);
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
    public List<NotificationResponseDTO> findByUserEmail
            (@Valid @PathVariable String email) {
        return notificationService.findByUserEmail(email);
    }

    @Operation(summary = "Создать сообщение другому юзеру",
            description = "Регистрирует новое сообщение и создает личный чат")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Сообщение создано"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PostMapping("/user/notif/create_personal_chat")
    public NotificationResponseForUserDTO createForCreatedPersonalChat
            (@AuthenticationPrincipal UserDetails user,
             @Valid @RequestBody NotificationCreateDTO notificationCreateDTO) {
        return notificationService.createForCreatedPersonalChat
                        (user.getUsername(), notificationCreateDTO);
    }


    @Operation(summary = "Создать сообщение в чат",
            description = "Регистрирует новое сообщение и отправляет в чат")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Сообщение создано"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PostMapping("/user/notif/create_in_chat")
    public ResponseToNotificationCreationForUser createInChatForUser
            (@AuthenticationPrincipal UserDetails emailUser,
             @Valid @RequestBody NotificationCreateInChatForUserDto dto) {
        return notificationService.createInChatForUser(emailUser.getUsername(), dto);
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


    @Operation(summary="Ищет сообщение пользователя по названию",
            description = "Ищет сообщение по сообщению")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщения найдены"),
            @ApiResponse(responseCode = "404", description = "Сообщения не найдены"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/mynotif/message/{message}")
    public List<NotificationResponseForUserDTO> findByMessageAndUserEmail
            (@PathVariable String message,
             @AuthenticationPrincipal UserDetails currentUser) {
        return notificationService.findByMessageAndUserEmail(message, currentUser.getUsername());
    }

    @Operation(summary="Ищет сообщение пользователя по чату",
            description = "Ищет сообщение по чату")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщения найдены"),
            @ApiResponse(responseCode = "404", description = "Сообщения не найдены"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/mynotif/chatId/{chatId}")
    public List<NotificationResponseForUserDTO> findByChatIdAndUserEmail
            (@Valid @PathVariable Long chatId, @AuthenticationPrincipal UserDetails userEmail) {
        return notificationService.findByChatIdAndUserEmail(chatId, userEmail.getUsername());
    }
}
