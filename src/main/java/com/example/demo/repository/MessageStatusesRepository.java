package com.example.demo.repository;

import com.example.demo.entity.MessageStatuses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageStatusesRepository extends JpaRepository<MessageStatuses, Integer> {
    @Query("SELECT ms FROM MessageStatuses ms " +
            "WHERE ms.user.id = :userId " +
            "AND ms.notification.message LIKE %:message%")
    List<MessageStatuses> findByUserIdAndNotificationId_MessageContaining(
            @Param("userId") Long userId,
            @Param("message") String message);

    List<MessageStatuses> findByUserId(Long user);
    List<MessageStatuses> findByUserEmail(String email);
    List<MessageStatuses> findByNotificationId(Long notification);
    List<MessageStatuses> findByNotificationMessage(String message);

    @Query("SELECT ms FROM MessageStatuses ms " +
            "WHERE ms.notification.chat.id = :chatId " +
            "AND ms.user.email = :userEmail")
    List<MessageStatuses> findByChatIdAndUserEmail(@Param("chatId") Long chatId,
                                                   @Param("userEmail") String userEmail);
}
