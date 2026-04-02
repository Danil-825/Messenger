package com.example.demo.DTO.AdminDTO;

import com.example.demo.entity.Participant;
import com.example.demo.entity.enums.ChatRole;
import lombok.Data;

@Data
public class ParticipantResponseForAdminDto {
    private Long id;
    private String email;
    private ChatRole chatRole;
    private String chat;

    public ParticipantResponseForAdminDto(Participant participant) {
        this.id = participant.getId();
        this.email = participant.getUser().getEmail();
        this.chatRole = participant.getChatRole();
        this.chat = participant.getChat().getTitle();
    }
}
