package com.example.demo.controllers;

import com.example.demo.DTO.AdminDTO.ParticipantResponseForAdminDto;
import com.example.demo.DTO.UserDTO.ParticipantResponseForUserDto;
import com.example.demo.services.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ParticipantController {
    private final ParticipantService participantService;


    @Operation(summary="Ищет участников по чату где он есть")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Участники найдены"),
            @ApiResponse(responseCode = "404", description = "Участники не найдены"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/participant/email/{chatId}")
    public List<ParticipantResponseForUserDto> findByChatIdForUser
            (@AuthenticationPrincipal UserDetails currentUser, @PathVariable Long chatId) {
        return participantService.findByChatIdForUser(currentUser.getUsername(), chatId);
    }


    @Operation(summary="Ищет участников по чату")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Участники найдены"),
            @ApiResponse(responseCode = "404", description = "Участники не найдены"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/participant/email/{chatId}")
    public List<ParticipantResponseForAdminDto> findByChatId(@PathVariable Long chatId) {
        return participantService.findByChatId(chatId);
    }
}
