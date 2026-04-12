package com.example.demo.services;

import com.example.demo.entity.MessageStatuses;
import com.example.demo.entity.Notification;
import com.example.demo.mypackage.utils.CollectionUtils;
import com.example.demo.repository.MessageStatusesRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncNotificationService {

    private final UserRepository userRepository;
    private final MessageStatusesRepository messageStatusesRepository;

    @Async("taskExecutor")
    public void createStatusesForParticipants(
            Notification notification, Long senderId, List<Long> participantsId) {

        List<List<Long>> batches = CollectionUtils.partition(participantsId, 100);
        batches.parallelStream().forEach(batch -> {
            List<MessageStatuses> statuses = new ArrayList<>();
            for (Long userId : batch) {
                String status = userId.equals(senderId) ? "отправлено" : "получено";
                statuses.add(MessageStatuses.builder()
                        .notification(notification)
                        .user(userRepository.getReferenceById(userId))
                        .status(status)
                        .build());
            }
            messageStatusesRepository.saveAll(statuses);
        });
    }

}
