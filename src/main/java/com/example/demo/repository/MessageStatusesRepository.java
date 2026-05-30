package com.example.demo.repository;

import com.example.demo.entity.MessageStatuses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageStatusesRepository extends JpaRepository<MessageStatuses, Integer> {
    @Query("SELECT ms FROM MessageStatuses ms " +
            "WHERE ms.user.id = :userId " +
            "AND ms.notification.message LIKE %:message%")
    List<MessageStatuses> findByUserIdAndNotificationId_MessageContaining(
            @Param("userId") Long userId,
            @Param("message") String message);

    @Query("SELECT DISTINCT ms FROM MessageStatuses ms " +
            "LEFT JOIN FETCH ms.notification n " +
            "LEFT JOIN FETCH n.user nu " +
            "LEFT JOIN FETCH ms.user mu " +
            "WHERE ms.user.id = :userId")
    List<MessageStatuses> findByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT ms FROM MessageStatuses ms " +
            "LEFT JOIN FETCH ms.notification n " +
            "LEFT JOIN FETCH n.user nu " +
            "LEFT JOIN FETCH ms.user mu " +
            "WHERE mu.email = :email")
    List<MessageStatuses> findByUserEmail(@Param("email") String email);

    @Query("SELECT DISTINCT ms FROM MessageStatuses ms " +
            "LEFT JOIN FETCH ms.notification n " +
            "LEFT JOIN FETCH n.user nu " +
            "LEFT JOIN FETCH ms.user mu " +
            "WHERE n.id = :notificationId")
    Optional<MessageStatuses> findByNotificationId(@Param("notificationId") Long notification);

    @Query("SELECT DISTINCT ms FROM MessageStatuses ms " +
            "LEFT JOIN FETCH ms.notification n " +
            "LEFT JOIN FETCH n.user nu " +
            "LEFT JOIN FETCH ms.user mu " +
            "WHERE n.message LIKE %:message%")
    List<MessageStatuses> findByNotificationMessage(@Param("message") String message);

    @Query("SELECT DISTINCT ms FROM MessageStatuses ms " +
            "LEFT JOIN FETCH ms.notification n " +
            "LEFT JOIN FETCH n.user nu " +
            "LEFT JOIN FETCH ms.user mu " +
            "LEFT JOIN FETCH n.chat c " +
            "WHERE c.id = :chatId AND mu.email = :userEmail")
    List<MessageStatuses> findByChatIdAndUserEmail(@Param("chatId") Long chatId,
                                                   @Param("userEmail") String userEmail);
}
