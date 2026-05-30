package com.example.demo.DTO.UserDTO;

import com.example.demo.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class ChatResponseForUserDto {
    private String title;
    private String type;

    public ChatResponseForUserDto(Chat chat) {
        this.title = chat.getTitle();
        this.type = chat.getType();
    }

    public static ChatResponseForUserDto group(Chat chat) {
        return new ChatResponseForUserDto(chat.getTitle(), chat.getType());
    }
}
