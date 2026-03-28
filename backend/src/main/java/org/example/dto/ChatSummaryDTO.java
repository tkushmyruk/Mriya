package org.example.dto; // Твій пакет для DTO

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSummaryDTO {

    private String chatId;

    private Integer interlocutorId;

    private String interlocutorName;

    private String lastMessage;

    private Date lastMessageTime;

    private Integer unreadCount;

    private String avatarUrl;
}