package com.example.demo.services;

import com.example.demo.DTO.UserDTO.NotificationCreateDTO;
import com.example.demo.DTO.AdminDTO.NotificationResponseDTO;
import com.example.demo.DTO.UserDTO.NotificationCreateInChatForUserDto;
import com.example.demo.DTO.UserDTO.NotificationResponseForUserDTO;
import com.example.demo.DTO.UserDTO.ResponseToNotificationCreationForUser;
import com.example.demo.entity.Chat;
import com.example.demo.entity.MessageStatuses;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.exceptions.ChatAlreadyExistsException;
import com.example.demo.exceptions.NotificationNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final MessageStatusesRepository messageStatusesRepository;
    private final ParticipantRepository participantRepository;
    private final AsyncNotificationService asyncNotificationService;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository, ChatService chatService,
                               ChatRepository chatRepository,
                               MessageStatusesRepository messageStatusesRepository,
                               ParticipantRepository participantRepository,
                               AsyncNotificationService asyncNotificationService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
        this.chatRepository = chatRepository;
        this.messageStatusesRepository = messageStatusesRepository;
        this.participantRepository = participantRepository;
        this.asyncNotificationService = asyncNotificationService;
    }


    @Transactional(rollbackFor = Exception.class)
    public NotificationResponseForUserDTO createForCreatedPersonalChat
            (String emailUser, NotificationCreateDTO dto) {
        User userSender = userRepository.findByEmail(emailUser)
                .orElseThrow(() ->
                        new UserNotFoundException("Not found user by email: "
                                + emailUser));
        User anotherUser = userRepository
                .findByEmail(dto.getAnotherEmailUser())
                .orElseThrow(() ->
                        new UserNotFoundException("Not found user by email: "
                                + dto.getAnotherEmailUser()));
        List<MessageStatuses> messageStatuses;
        if (!chatRepository.existsPersonalChatBetweenUsers
                (emailUser, dto.getAnotherEmailUser()) &&
                anotherUser.getUserRole().equals(UserRole.USER)) {
            Chat chat = chatService.createPersonalChat
                    (emailUser, dto.getAnotherEmailUser());
            Notification notification = Notification.builder()
                    .message(dto.getMessage())
                    .user(userSender)
                    .chat(chat)
                    .build();
            notificationRepository.save(notification);

            messageStatuses = List.of(
                    MessageStatuses.builder()
                            .notification(notification)
                            .user(userSender)
                            .status("отправлено")
                            .build(),
                    MessageStatuses.builder()
                            .notification(notification)
                            .user(anotherUser)
                            .status("получено")
                            .build()
            );

            messageStatusesRepository.saveAll(messageStatuses);
        } else {
            log.warn("Такой чат уже есть");
            throw new ChatAlreadyExistsException("Такой чат уже есть");
        }
        return new NotificationResponseForUserDTO(messageStatuses.get(0));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseToNotificationCreationForUser createInChatForUser
            (String emailUser, NotificationCreateInChatForUserDto dto) {
        User userSender = userRepository.findByEmail(emailUser)
                .orElseThrow(() ->
                        new UserNotFoundException("Not found user by email: " + emailUser));
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        Notification notification = Notification.builder()
                .message(dto.getMessage())
                .user(userSender)
                .chat(chat)
                .build();
        notificationRepository.save(notification);

        List<Long> participantsId = participantRepository.findUserIdsByChatId(chat.getId());

        asyncNotificationService.createInChatForUser
                (notification, userSender.getId(), participantsId);
        return new ResponseToNotificationCreationForUser(notification);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete (Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public List<NotificationResponseDTO> findByUserId(Long userId) {
        List<MessageStatuses> notifications = messageStatusesRepository.findByUserId(userId);
        checkNotEmpty(notifications);
        return notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> findByUserEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException
                        ("Not found user by email: " + userEmail));
        List<MessageStatuses> notifications = messageStatusesRepository
                .findByUserEmail(user.getEmail());
        checkNotEmpty(notifications);
        return notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> findById(Long notificationId) {
        List<MessageStatuses> notifications = messageStatusesRepository
                .findByNotificationId(notificationId);
        checkNotEmpty(notifications);
        return notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());

    }

    public List<NotificationResponseDTO> findByMessage(String message) {
        List<MessageStatuses> notifications = messageStatusesRepository
                .findByNotificationMessage(message);
        checkNotEmpty(notifications);
        return notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseForUserDTO> findByMessageAndUserEmail
            (String message, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException
                        ("Not found user by email: " + userEmail));
        List<MessageStatuses> notifications = messageStatusesRepository
                .findByUserIdAndNotificationId_MessageContaining(user.getId(), message);
        checkNotEmpty(notifications);
        return notifications.stream()
                .map(NotificationResponseForUserDTO::new)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseForUserDTO> findByChatIdAndUserEmail
            (Long chatId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException
                        ("Not found user by email: " + userEmail));
        Chat chat = chatRepository
                .findByParticipantUserEmailAndId(chatId, user.getEmail())
                .orElseThrow(() -> new RuntimeException("Not found chat"));
        List<MessageStatuses> notifications = messageStatusesRepository
                .findByChatIdAndUserEmail(chat.getId(), user.getEmail());
        checkNotEmpty(notifications);
        return notifications.stream()
                .map(NotificationResponseForUserDTO::new)
                .collect(Collectors.toList());
     }


    private <T> void checkNotEmpty(List<T> list) {
        if (list == null || list.isEmpty()) {
            log.warn("Notifications not found");
            throw new NotificationNotFoundException("Notifications not found");
        }
    }

}