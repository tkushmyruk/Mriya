package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.sql.User;
import org.example.repository.sql.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    public void sendNotification(long authorId, long userId) {

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        messagingTemplate.convertAndSendToUser(
                author.getEmail(),
                "/queue/notifications",
                Map.of(
                        "type", "LIKE",
                        "message", user.getProfile().getFirstName() + " " + user.getProfile().getLastName() + " лайкнув твій пост!"
                )
        );
    }
}