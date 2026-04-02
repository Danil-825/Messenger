package com.example.demo.DTO.UserDTO;

import com.example.demo.entity.Participant;
import com.example.demo.entity.enums.ChatRole;
import lombok.Data;

@Data
public class ParticipantResponseForUserDto {
    private String email;
    private ChatRole chatRole;
    private String chat;

    public ParticipantResponseForUserDto(Participant participant) {
        this.email = participant.getUser().getEmail();
        this.chatRole = participant.getChatRole();
        this.chat = participant.getChat().getTitle();
    }
}
