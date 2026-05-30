package com.example.demo.DTO.AdminDTO;

import com.example.demo.entity.Chat;
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

    public static ChatResponseForAdminDto group(Chat chat) {
        return new ChatResponseForAdminDto(chat.getId(), chat.getTitle(), chat.getType());
    }
}
