package com.example.demo.repository;

import com.example.demo.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    boolean existsByChatIdAndUserId(Long chatId, Long userId);

    @Query("SELECT p.user.id FROM Participant p WHERE p.chat.id = :chatId")
    List<Long> findUserIdsByChatId(@Param("chatId") Long chatId);

    @Query("SELECT p.user.email FROM Participant p WHERE p.chat.id = :chatId AND p.chat.type = 'PERSONAL'")
    List<String> findUserEmailsByChatId(@Param("chatId") Long chatId);

    List<Participant> findByChatId(Long chatId);

    @Query("SELECT p.user.email FROM Participant p " +
            "WHERE p.chat.id = :chatId AND p.user.id != :userId")
    Optional<String> findCompanionEmailForPersonalChat(@Param("chatId") Long chatId,
                                                       @Param("userId") Long userId);

    @Query("SELECT p.chat.id, p.user.email FROM Participant p " +
            "WHERE p.chat.id IN :chatIds AND p.user.id != :userId")
    List<Object[]> findCompanionsForPersonalChats(@Param("chatIds") List<Long> chatIds,
                                                  @Param("userId") Long userId);
}
