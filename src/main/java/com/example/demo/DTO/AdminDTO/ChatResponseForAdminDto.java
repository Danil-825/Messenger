package com.example.demo.DTO.AdminDTO;

import com.example.demo.DTO.UserDTO.ChatResponseForUserDto;
import com.example.demo.entity.Chat;
import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChatResponseForAdminDto {
    private Long id;
    private String title;
    private String type;

    public ChatResponseForAdminDto(Chat chat) {
        this.id = chat.getId();
        this.title = chat.getTitle();
        this.type = chat.getType();
    }

    public static ChatResponseForAdminDto personal(Chat chat, User user) {
        return new ChatResponseForAdminDto(chat.getId(), user.getEmail(), chat.getType());
    }

    public static ChatResponseForAdminDto group(Chat chat) {
        return new ChatResponseForAdminDto(chat.getId(), chat.getTitle(), chat.getType());
    }
}
