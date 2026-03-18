package com.example.demo.services;

import com.example.demo.DTO.UserDTO.NotificationCreateDTO;
import com.example.demo.DTO.AdminDTO.NotificationResponseDTO;
import com.example.demo.DTO.UserDTO.NotificationResponseForUserDTO;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.exceptions.NotificationNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }


    @Transactional(rollbackFor = Exception.class)
    public NotificationResponseForUserDTO create (String emailUser, NotificationCreateDTO notificationCreateDTO) {
        User userSender = userRepository.findByEmail(emailUser)
                .orElseThrow(() -> new UserNotFoundException("not found"));
        User anotherUser = userRepository.findByEmail(notificationCreateDTO.getAnotherEmailUser())
                .orElseThrow(() -> {
                    log.warn("User not found");
                    return new UserNotFoundException("User not found");
                });
        if (anotherUser.getUserRole().equals(UserRole.USER)) {
            Notification notification = new Notification();
            notification.setMessage(notificationCreateDTO.getMessage());
            notification.setStatus("отправлено");
            notification.setUser(userSender);
            notification.setAnotherUser(anotherUser);
            notificationRepository.save(notification);

            Notification anotherNotification = new Notification();
            anotherNotification.setMessage(notificationCreateDTO.getMessage());
            anotherNotification.setStatus("получено");
            anotherNotification.setUser(anotherUser);
            anotherNotification.setAnotherUser(userSender);
            notificationRepository.save(anotherNotification);

            return new NotificationResponseForUserDTO(notification);
        }
        else {
            log.warn("Пользователь является админом");
            throw new RuntimeException("Not found");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete (Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }


    public List<NotificationResponseDTO> findAll() {
        checkNotification(notificationRepository.findAll());
        return notificationRepository.findAll()
                .stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> findByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        checkNotification(notifications);
        return notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseForUserDTO> findByUserEmail(String email) {
        List<Notification> notifications = notificationRepository.findByUserEmail(email);
        checkNotification(notifications);
        return notifications.stream()
                .map(NotificationResponseForUserDTO::new)
                .collect(Collectors.toList());
    }

    public NotificationResponseDTO findById(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        Notification notificationEntity = notification
                .orElseThrow(() -> new RuntimeException("not found"));
        return new NotificationResponseDTO(notificationEntity);
    }

    public List<NotificationResponseDTO> findByMessage(String message) {
        List<Notification> notifications = notificationRepository.findByMessage(message);
        checkNotification(notifications);
        return notifications.stream()
                .map(NotificationResponseDTO::new)
                . collect(Collectors.toList());
    }

    public List<NotificationResponseForUserDTO> findByMessageAndUserEmail(String message, String userEmail) {
        List<Notification> notifications = notificationRepository.findByMessageAndUserEmail(message, userEmail);
        checkNotification(notifications);
        return notifications.stream()
                .map(NotificationResponseForUserDTO::new)
                .collect(Collectors.toList());
    }


    private void checkNotification(List<Notification> notification) {
        if (notification.isEmpty()) {
            log.warn("Not found");
            throw new NotificationNotFoundException("Notification not found");
        }
    }
}
