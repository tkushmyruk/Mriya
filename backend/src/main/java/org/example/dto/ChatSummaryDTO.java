package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSummaryDTO {

    private String chatId;

    private Long interlocutorId;

    private String interlocutorName;

    private String lastMessage;

    private Date lastMessageTime;

    private Integer unreadCount;

    private String avatarUrl;
}