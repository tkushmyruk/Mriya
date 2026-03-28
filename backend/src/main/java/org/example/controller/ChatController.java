package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.nosql.Message;
import org.example.domain.sql.User;
import org.example.dto.ChatSummaryDTO;
import org.example.dto.MessageDTO;
import org.example.repository.sql.UserRepository;
import org.example.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void processMessage(@Payload MessageDTO chatMessage, Principal principal) {
        User sender = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User recipient = userRepository.findById(chatMessage.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        chatMessage.setSenderId(sender.getId());
        MessageDTO saved = messageService.saveMessage(chatMessage);

        messagingTemplate.convertAndSendToUser(
                recipient.getEmail(),
                "/queue/messages",
                saved
        );

        messagingTemplate.convertAndSendToUser(
                sender.getEmail(),
                "/queue/messages",
                saved
        );
    }
    @GetMapping("/chats")
    public ResponseEntity<List<ChatSummaryDTO>> getChatSummaries( Principal principal) {
        Integer userId = userRepository.findByEmail(principal.getName()).get().getId();
        return ResponseEntity.ok(messageService.getChatSummaries(userId));
    }

    @GetMapping("/history/{interlocutorId}")
    public ResponseEntity<List<MessageDTO>> getChatHistory(Principal principal, @PathVariable Integer interlocutorId) {
        Integer currentUserId = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        return ResponseEntity.ok(messageService.getChatHistory(currentUserId, interlocutorId));
    }
}
