package com.example.demo.services;

import com.example.demo.entity.*;
import com.example.demo.entity.enums.ChatRole;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessageStatusesRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class BroadcastService {

    private final MessageStatusesRepository messageStatusesRepository;
    private final ChatRepository chatRepository;
    private final NotificationRepository notificationRepository;
    private final ParticipantRepository participantRepository;

    @Async("broadcastExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Integer> sendBatchAsync(User userAdmin, String message, List<User> batch) {
        int errors = 0;
        for (User user : batch) {
            try {
                sendToSingleUser(userAdmin, message, user);
            } catch (Exception e) {
                errors++;
                log.error("Ошибка отправки пользователю {}: {}", user.getId(), e.getMessage(), e);
            }
        }
        return CompletableFuture.completedFuture(errors);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendToSingleUser(User userAdmin, String message, User user) {
        Chat chat = getOrCreatePersonalChat(userAdmin, user);

        Notification notification = notificationRepository.save(
                Notification.builder()
                        .message(message)
                        .user(user)
                        .chat(chat)
                        .build()
        );

        messageStatusesRepository.save(MessageStatuses.builder()
                .notification(notification)
                .user(userAdmin)
                .status("отправлено")
                .build());

        messageStatusesRepository.save(MessageStatuses.builder()
                .notification(notification)
                .user(user)
                .status("получено")
                .build());
    }

    private Chat getOrCreatePersonalChat(User userAdmin, User user) {
        return chatRepository.findPersonalChatByUserEmails(user.getEmail(), userAdmin.getEmail())
                .orElseGet(() -> {
                    Chat newChat = Chat.builder()
                            .title(null)
                            .type("PERSONAL")
                            .build();
                    Chat savedChat = chatRepository.save(newChat);

                    participantRepository.save(Participant.builder()
                            .user(userAdmin)
                            .chat(savedChat)
                            .chatRole(ChatRole.ADMIN)
                            .build());

                    participantRepository.save(Participant.builder()
                            .user(user)
                            .chat(savedChat)
                            .chatRole(ChatRole.MEMBER)
                            .build());

                    return savedChat;
                });
    }
}