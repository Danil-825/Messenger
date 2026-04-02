package com.example.demo.services;

import com.example.demo.DTO.AdminDTO.ParticipantResponseForAdminDto;
import com.example.demo.DTO.UserDTO.ParticipantResponseForUserDto;
import com.example.demo.entity.Participant;
import com.example.demo.entity.User;
import com.example.demo.exceptions.NotificationNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repository.ParticipantRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public List<ParticipantResponseForUserDto> findByChatIdForUser(String email, Long chatId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        boolean isParticipant = participantRepository.existsByChatIdAndUserId(chatId, user.getId());
        if (!isParticipant) {
            throw new AccessDeniedException("Вы не являетесь участником чата: " + chatId);
        }
        List<Participant> participants = participantRepository.findByChatId(chatId);
        checkNotEmpty(participants);
        return participants.stream()
                .map(ParticipantResponseForUserDto::new)
                .collect(Collectors.toList());
    }

    public List<ParticipantResponseForAdminDto> findByChatId(Long chatId) {
        List<Participant> participants = participantRepository.findByChatId(chatId);
        checkNotEmpty(participants);
        return participants.stream()
                .map(ParticipantResponseForAdminDto::new)
                .collect(Collectors.toList());
    }

    private <T> void checkNotEmpty(List<T> list) {
        if (list == null || list.isEmpty()) {
            log.warn("Participants not found");
            throw new NotificationNotFoundException("Participants not found");
        }
    }
}
