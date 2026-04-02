package com.example.demo.services;

import com.example.demo.entity.MessageStatuses;
import com.example.demo.entity.Notification;
import com.example.demo.repository.MessageStatusesRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncNotificationService {

    private final UserRepository userRepository;
    private final MessageStatusesRepository messageStatusesRepository;


    public void createInChatForUser
            (Notification notification, Long senderId, List<Long> participantsId) {
        log.info("Выполняется в потоке: {}", Thread.currentThread().getName());
        List<MessageStatuses> statuses = new ArrayList<>();

        for (Long userId : participantsId) {
            String status = userId.equals(senderId) ? "отправлено" : "получено";
            MessageStatuses messageStatuses = MessageStatuses.builder()
                    .notification(notification)
                    .user(userRepository.getReferenceById(userId))
                    .status(status)
                    .build();
            statuses.add(messageStatuses);
        }
        messageStatusesRepository.saveAll(statuses);

        log.info("Создано {} статусов для сообщения {}", statuses.size(), notification.getId());
    }
}
