package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private String id;
    private Integer senderId;
    private Integer recipientId;
    private String content;
    private LocalDateTime sentAt;
    private String status;

}