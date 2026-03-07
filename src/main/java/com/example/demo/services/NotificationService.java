package com.example.demo.services;

import com.example.demo.DTO.UserDTO.NotificationCreateDTO;
import com.example.demo.DTO.AdminDTO.NotificationResponseDTO;
import com.example.demo.DTO.UserDTO.NotificationResponseForUserDTO;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public NotificationResponseForUserDTO create (String emailUser, NotificationCreateDTO notificationCreateDTO) {
        User userSender = userRepository.findByEmail(emailUser)
                .orElseThrow(() -> new RuntimeException("not found"));
        User anotherUser = userRepository.findByEmail(notificationCreateDTO.getAnotherEmailUser())
                .orElseThrow(() -> new RuntimeException("not found"));
        if (anotherUser.getUserRole().equals(UserRole.USER)) {
            Notification notification = new Notification();
            notification.setTitle(notificationCreateDTO.getTitle());
            notification.setContent(notificationCreateDTO.getContent());
            notification.setStatus("отправлено");
            notification.setUser(userSender);
            notification.setAnotherUser(anotherUser);
            notificationRepository.save(notification);

            Notification anotherNotification = new Notification();
            anotherNotification.setTitle(notificationCreateDTO.getTitle());
            anotherNotification.setContent(notificationCreateDTO.getContent());
            anotherNotification.setStatus("получено");
            anotherNotification.setUser(anotherUser);
            anotherNotification.setAnotherUser(userSender);
            notificationRepository.save(anotherNotification);

            return new NotificationResponseForUserDTO(notification);
        }
        else throw new RuntimeException("Not found");
    }

    public void delete (Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public List<NotificationResponseDTO> findAll() {
        return notificationRepository
                .findAll()
                .stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> findByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        return notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseForUserDTO> findByUserEmail(String email) {
        List<Notification> notifications = notificationRepository.findByUserEmail(email);
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

    public List<NotificationResponseDTO> findByTitle(String title) {
        List<Notification> notifications = notificationRepository.findByTitle(title);
        return notifications.stream()
                .map(NotificationResponseDTO::new)
                . collect(Collectors.toList());
    }

    public NotificationResponseDTO findByTitleAndUserEmail(String title, String userEmail) {
        Optional <Notification> notification = notificationRepository
                .findByTitleAndUserEmail(title, userEmail);
        Notification notif = notification.orElseThrow(() -> new RuntimeException("Not found"));
        return new NotificationResponseDTO(notif);
    }
}
