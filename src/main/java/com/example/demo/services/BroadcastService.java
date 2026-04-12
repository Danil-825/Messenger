package com.example.demo.services;

import com.example.demo.entity.*;
import com.example.demo.entity.enums.ChatRole;
import com.example.demo.exceptions.ChatNotFoundException;
import com.example.demo.mypackage.utils.CollectionUtils;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessageStatusesRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BroadcastService {

    private final MessageStatusesRepository messageStatusesRepository;
    private final ChatRepository chatRepository;
    private final NotificationRepository notificationRepository;
    private final ParticipantRepository participantRepository;

    @Async("broadcastExecutor")
    public void broadcastToAllUsers(User userAdmin, String message, List<User> allUsers) {
        List<List<User>> batches = CollectionUtils.partition(allUsers, 100);

        batches.parallelStream().forEach(batch -> {
            List<MessageStatuses> statuses = new ArrayList<>();
            List<Chat> chats = new ArrayList<>();
            List<Participant> participants = new ArrayList<>();
            List<Notification> notifications = new ArrayList<>();
            Chat chat;
            for (User user : batch) {
                if (!chatRepository.existsPersonalChatBetweenUsers(user.getEmail(), userAdmin.getEmail())) {
                    chat = Chat.builder()
                            .title(null)
                            .type("PERSONAL")
                            .build();
                    chats.add(chat);
                } else {
                    chat = chatRepository.findPersonalChatByUserEmails(user.getEmail(), userAdmin.getEmail())
                            .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
                }

                Participant participantAdmin = Participant.builder()
                        .user(userAdmin)
                        .chat(chat)
                        .chatRole(ChatRole.ADMIN)
                        .build();
                participants.add(participantAdmin);

                Participant participantUser = Participant.builder()
                        .user(user)
                        .chat(chat)
                        .chatRole(ChatRole.MEMBER)
                        .build();
                participants.add(participantUser);

                Notification notification = Notification.builder()
                        .message(message)
                        .user(user)
                        .chat(chat)
                        .build();
                notifications.add(notification);

                MessageStatuses statusSend = MessageStatuses.builder()
                        .notification(notification)
                        .user(userAdmin)
                        .status("отправлено")
                        .build();
                statuses.add(statusSend);

                MessageStatuses statusReceive = MessageStatuses.builder()
                        .notification(notification)
                        .user(user)
                        .status("получено")
                        .build();
                statuses.add(statusReceive);
            }
            chatRepository.saveAll(chats);
            participantRepository.saveAll(participants);
            notificationRepository.saveAll(notifications);
            messageStatusesRepository.saveAll(statuses);
        });
    }
}
