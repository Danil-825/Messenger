package com.example.demo.services;

import com.example.demo.entity.MessageStatuses;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Participant;

import com.example.demo.mypackage.utils.CollectionUtils;
import com.example.demo.repository.MessageStatusesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncNotificationService {

    private final MessageStatusesRepository messageStatusesRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createStatusesForParticipants(
            Notification notification, Long senderId, List<Participant> participants) {

        List<List<Participant>> batches = CollectionUtils.partition(participants, 100);

        for (List<Participant> batch : batches) {
            List<MessageStatuses> statuses = batch.stream()
                    .map(participant -> MessageStatuses.builder()
                            .notification(notification)
                            .user(participant.getUser())
                            .status(participant.getId().equals(senderId) ? "отправлено" : "получено")
                            .build())
                    .collect(Collectors.toList());

            messageStatusesRepository.saveAll(statuses);
        }

        CompletableFuture.completedFuture(null);
    }

}
