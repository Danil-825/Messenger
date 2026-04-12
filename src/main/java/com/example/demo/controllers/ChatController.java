package com.example.demo.controllers;

import com.example.demo.DTO.AdminDTO.ChatResponseForAdminDto;
import com.example.demo.DTO.UserDTO.ChatCreateDtoForUser;
import com.example.demo.DTO.UserDTO.ChatResponseForUserDto;
import com.example.demo.services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @Operation(summary="Ищет чат по его id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Чат найден"),
            @ApiResponse(responseCode = "404", description = "Чат не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/chat/id/{id}")
    public ChatResponseForAdminDto findChatById(@PathVariable Long id) {
        return chatService.findChatById(id);
    }


    @Operation(summary="Ищет все чаты")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Чаты найдены"),
            @ApiResponse(responseCode = "404", description = "Чаты не найдены"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/chats")
    public List<ChatResponseForAdminDto> findAllChat() {
        return chatService.findAllChat();
    }


    @Operation(summary="Ищет чат по его названию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Чат найден"),
            @ApiResponse(responseCode = "404", description = "Чат не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/chat/title/{title}")
    public List<ChatResponseForAdminDto> findByTitle(@PathVariable String title) {
        return chatService.findByTitle(title);
    }


    @Operation(summary="Ищет чаты по email участника, состоящего в них")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Чаты найдены"),
            @ApiResponse(responseCode = "404", description = "Чаты не найдены"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/chat/participants/email/{email}")
    public List<ChatResponseForAdminDto> findByParticipantUserEmail(@Valid @PathVariable String email){
        return chatService.findByParticipantUserEmail(email);
    }


    @Operation(summary="Удаление чата")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Чат удален"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "404", description = "Чат не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/chat/delete/{chatId}")
    public void deleteGroupChat(@PathVariable Long chatId) {
        chatService.deleteGroupChat(chatId);
    }


    @Operation(summary = "Создать групповой чат",
            description = "Регистрирует новый чат")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Чат создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PostMapping("/user/group_chat/create")
    public ChatResponseForUserDto createGroupChat
            (@AuthenticationPrincipal UserDetails currentUser, @Valid @RequestBody ChatCreateDtoForUser dto) {
        return chatService.createGroupChat(currentUser.getUsername(), dto);
    }


    @Operation(summary="Ищет чат по его id для юзера")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Чат найден"),
            @ApiResponse(responseCode = "404", description = "Чат не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/chat/id/{id}")
    public ChatResponseForUserDto findChatByIdForUser
            (@AuthenticationPrincipal UserDetails currentEmail, @Valid @PathVariable Long id) {
        return chatService.findChatByIdForUser(currentEmail.getUsername(), id);
    }


    @Operation(summary="Ищет все чаты в которых есть юзер")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Чаты найдены"),
            @ApiResponse(responseCode = "404", description = "Чаты не найдены"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/my_chats")
    public List<ChatResponseForUserDto> findAllChatForUser
            (@AuthenticationPrincipal UserDetails currentEmail) {
        return chatService.findAllChatForUser(currentEmail.getUsername());
    }


    @Operation(summary="Ищет чат по его названию для юзера")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Чат найден"),
            @ApiResponse(responseCode = "404", description = "Чат не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/chat/title/{title}")
    public List<ChatResponseForUserDto> findByTitleForUser
            (@AuthenticationPrincipal UserDetails currentEmail,
             @Valid @PathVariable String title) {
        return chatService.findByTitleForUser(currentEmail.getUsername(), title);
    }
}
