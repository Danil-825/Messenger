package com.example.demo.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMessage(String message);

    @Query("SELECT n FROM Notification n JOIN FETCH Chat c WHERE n.id =:id")
    Optional<Notification> findByIdWithChat(Long id);
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserEmail(String email);
    List<Notification> findByMessageAndUserEmail(String message, String email);
}
