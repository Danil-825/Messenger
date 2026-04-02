package com.example.demo.services;

import com.example.demo.DTO.AdminDTO.ChatResponseForAdminDto;
import com.example.demo.DTO.UserDTO.ChatCreateDtoForUser;
import com.example.demo.DTO.UserDTO.ChatResponseForUserDto;
import com.example.demo.entity.Chat;
import com.example.demo.entity.Participant;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.ChatRole;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.exceptions.ChatNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.ParticipantRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    public ChatResponseForAdminDto findChatById(Long id) {
        Optional<Chat> chat = chatRepository.findById(id);
        Chat chatDto = chat.orElseThrow(() -> new ChatNotFoundException("Not found chat by id: " + id));
        return checkingPersonalChatForAdmin(chatDto);
    }

    public List<ChatResponseForAdminDto> findAllChat() {
        List<Chat> chats = chatRepository.findAll();
        checkNotEmpty(chats);
        return chats.stream()
                .map(ChatResponseForAdminDto::new)
                .collect(Collectors.toList());
    }

    public List<ChatResponseForAdminDto> findByTitle(String title) {
        List<Chat> chats = chatRepository.findByTitle(title);
        checkNotEmpty(chats);
        return chats.stream()
                .map(ChatResponseForAdminDto::new)
                .collect(Collectors.toList());
    }

    public List<ChatResponseForAdminDto> findByParticipantUserEmail(String email) {
        List<Chat> chats = chatRepository.findByParticipantUserEmail(email);
        checkNotEmpty(chats);
        return chats.stream()
                .map(ChatResponseForAdminDto::new)
                .collect(Collectors.toList());
    }

    public ChatResponseForUserDto createGroupChat(String email, ChatCreateDtoForUser dto) {
        User emailUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("Not found user by email: " + email));
        Chat chat = new Chat();
        chat.setTitle(dto.getTitle());
        chat.setType("GROUP");
        chatRepository.save(chat);

        addParticipant(chat.getId(), emailUser.getId(), ChatRole.ADMIN);

        List<Long> addedMembers = new ArrayList<>();
        if (dto.getEmails() != null && !dto.getEmails().isEmpty()) {
            addedMembers =  addParticipantsByEmails(chat.getId(), dto.getEmails());
        }
        log.info("создан чат '{}', количество участников {}", chat.getTitle(), addedMembers.size()+1);
        return new ChatResponseForUserDto(chat);
    }

    public void deleteGroupChat(Long chatId) {
        Optional<Chat> chat = chatRepository.findById(chatId);
        Chat chatDto = chat.orElseThrow(() ->
                new ChatNotFoundException("Not found chat by id: " + chatId));
        chatRepository.deleteById(chatDto.getId());
    }

    public ChatResponseForUserDto findChatByIdForUser(String currentEmail, Long id) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundException
                        ("User not found: " + currentEmail));
        Optional<Chat> chat = chatRepository
                .findByParticipantUserEmailAndId(id, currentEmail);
        Chat chatDto = chat.orElseThrow(() -> new ChatNotFoundException
                ("Not found chat by id: " + id));
        return checkingPersonalChatForUser(chatDto, user.getId());
    }

    public List<ChatResponseForUserDto> findAllChatForUser(String currentEmail) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentEmail));

        List<Chat> chats = chatRepository.findByParticipantUserEmail(currentEmail);
        checkNotEmpty(chats);

        Map<Long, String> companionEmails = getCompanionEmailsForChats(chats, user.getId());
        return chats.stream()
                .map(chat -> toChatResponseDto(chat, companionEmails))
                .collect(Collectors.toList());
    }

    public List<ChatResponseForUserDto> findByTitleForUser(String currentEmail, String title) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundException
                        ("User not found: " + currentEmail));
        List<Chat> chats = chatRepository
                .findByTitleAndParticipantEmail(title, currentEmail);
        checkNotEmpty(chats);
        Map<Long, String> companionEmails = getCompanionEmailsForChats(chats, user.getId());
        return chats.stream()
                .map(chat -> toChatResponseDto(chat, companionEmails))
                .collect(Collectors.toList());
    }

    public Chat createPersonalChat(String email, String anotherEmail) {
        User userSender = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("Not found user by email: " + email));
        User anotherUser = userRepository.findByEmail(anotherEmail)
                .orElseThrow(() ->
                        new UserNotFoundException("Not found user by email: " + anotherEmail));
        Chat chat = new Chat();
        chat.setTitle(null);
        chat.setType("PERSONAL");
        chatRepository.save(chat);
        addParticipant(chat.getId(), userSender.getId(), ChatRole.ADMIN);
        addParticipant(chat.getId(), anotherUser.getId(), ChatRole.ADMIN);
        log.info("создан личный чат");
        return chat;
    }






    private void addParticipant(Long chatId, Long userId, ChatRole chatRole) {
        boolean alreadyExists = participantRepository.existsByChatIdAndUserId(chatId, userId);
        if (alreadyExists) {
            return;
        }
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Чат не найден"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (user.getUserRole().equals(UserRole.USER)) {
            Participant participant = Participant.builder()
                    .chat(chat)
                    .user(user)
                    .chatRole(chatRole)
                    .build();
            participantRepository.save(participant);
        } else {
            log.warn("User {} is ADMIN", user.getEmail());
            throw new RuntimeException("Этот пользователь является админом");
        }
    }

    private List<Long> addParticipantsByEmails(Long chatId, List<String> emails) {
        List<User> users = userRepository.findByEmailIn(emails);

        List<Long> addedUserIds = new ArrayList<>();
        for (User user : users) {
            addParticipant(chatId, user.getId(), ChatRole.MEMBER);
            addedUserIds.add(user.getId());
        }
        if (users.size() != emails.size()) {
            log.warn("Не все пользователи найдены для чата {}", chatId);
        }
        return addedUserIds;
    }

    private <T> void checkNotEmpty(List<T> list) {
        if (list == null || list.isEmpty()) {
            log.warn("Chat not found");
            throw new ChatNotFoundException("Chat not found");
        }
    }

    private ChatResponseForUserDto checkingPersonalChatForUser(Chat chatDto, Long userId) {
        String displayName;
        if (chatDto.getType().equals("PERSONAL")) {
            displayName = participantRepository
                    .findCompanionEmailForPersonalChat(chatDto.getId(), userId)
                    .orElse("Unknown User");
        } else {
            displayName = chatDto.getTitle();
        }
        return ChatResponseForUserDto.builder()
                .title(displayName)
                .type(chatDto.getType())
                .build();
    }

    private ChatResponseForUserDto toChatResponseDto(Chat chat, Map<Long, String> companionEmails) {
        String displayName;

        if (chat.getType().equals("PERSONAL")) {
            displayName = companionEmails.getOrDefault(chat.getId(), "Unknown User");
        } else {
            displayName = chat.getTitle();
        }

        return ChatResponseForUserDto.builder()
                .title(displayName)
                .type(chat.getType())
                .build();
    }

    private Map<Long, String> getCompanionEmailsForChats(List<Chat> chats, Long userId) {
        List<Long> personalChatIds = chats.stream()
                .filter(chat -> chat.getType().equals("PERSONAL"))
                .map(Chat::getId)
                .collect(Collectors.toList());

        if (personalChatIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Object[]> companions = participantRepository
                .findCompanionsForPersonalChats(personalChatIds, userId);

        Map<Long, String> result = new HashMap<>();
        for (Object[] row : companions) {
            Long chatId = ((Number) row[0]).longValue();
            String email = (String) row[1];
            result.put(chatId, email);
        }

        return result;
    }

    private ChatResponseForAdminDto checkingPersonalChatForAdmin(Chat chatDto) {
        String displayName;
        if (chatDto.getType().equals("PERSONAL")) {
            List<String> listEmail = participantRepository
                    .findUserEmailsByChatId(chatDto.getId());
            displayName = listEmail.get(0) + " and " + listEmail.get(1);
        } else {
            displayName = chatDto.getTitle();
        }
        return ChatResponseForAdminDto.builder()
                .id(chatDto.getId())
                .title(displayName)
                .type(chatDto.getType())
                .build();
    }

}
