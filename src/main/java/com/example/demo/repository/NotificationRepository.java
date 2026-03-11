package com.example.demo.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTitle(String title);
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserEmail(String email);
    List<Notification> findByTitleAndUserEmail(String title, String email);
}
