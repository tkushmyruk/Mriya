package org.example.domain.nosql;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(name = "chat_idx", def = "{'senderId': 1, 'recipientId': 1, 'sentAt': -1}")
public class Message {

    @Id
    private String id;

    private Long senderId;
    private Long recipientId;
    private String content;
    private LocalDateTime sentAt;

    private MessageStatus status;
}