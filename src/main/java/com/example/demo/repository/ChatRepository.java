package com.example.demo.repository;

import com.example.demo.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByTitle(String title);

    @Query("SELECT c FROM Chat c WHERE c.id = :id AND c.id IN " +
            "(SELECT p.chat.id FROM Participant p WHERE p.user.email = :email)")
    Optional<Chat> findByParticipantUserEmailAndId
            (@Param("id") Long id, @Param("email") String email);

    @Query("SELECT c FROM Chat c WHERE c.id IN " +
            "(SELECT p.chat.id FROM Participant p WHERE p.user.email = :email)")
    List<Chat> findByParticipantUserEmail(@Param("email") String email);

    @Query( "SELECT c FROM Chat c " +
            "JOIN Participant p1 ON c.id = p1.chat.id " +
            "JOIN Participant p2 ON c.id = p2.chat.id " +
            "WHERE c.type = 'PERSONAL' AND p1.user.email = :email1 AND p2.user.email = :email2")
    Optional<Chat> findPersonalChatByUserEmails
            (@Param("email1") String email1, @Param("email2") String email2);

    @Query("SELECT c FROM Chat c WHERE c.title = :title AND " +
            "EXISTS (SELECT 1 FROM Participant p WHERE p.chat.id = c.id AND p.user.email = :email)")
    List<Chat> findByTitleAndParticipantEmail(@Param("title") String title,
                                              @Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Chat c " +
            "JOIN Participant p1 ON c.id = p1.chat.id " +
            "JOIN Participant p2 ON c.id = p2.chat.id " +
            "WHERE c.type = 'PERSONAL' " +
            "AND p1.user.email = :email1 " +
            "AND p2.user.email = :email2")
    boolean existsPersonalChatBetweenUsers(@Param("email1") String email1, @Param("email2") String email2);

    @Query("SELECT c FROM Chat c WHERE c.type = 'PERSONAL' AND c.id IN " +
            "(SELECT p.chat.id FROM Participant p WHERE p.user.id = :id)")
    List<Long> findPersonalChatsByParticipantUserId(@Param("id") Long id);
}


