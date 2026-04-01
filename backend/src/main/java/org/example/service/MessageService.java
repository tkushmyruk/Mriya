package org.example.service;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.example.domain.nosql.Message;
import org.example.domain.nosql.MessageStatus;
import org.example.domain.sql.User;
import org.example.dto.ChatSummaryDTO;
import org.example.dto.MessageDTO;
import org.example.repository.nosql.MessageRepository;
import org.example.repository.sql.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public MessageDTO saveMessage(MessageDTO dto) {
        if (!userRepository.existsById(dto.getSenderId()) ||
                !userRepository.existsById(dto.getRecipientId())) {
            throw new RuntimeException("Sender or Recipient not found in SQL database");
        }

        Message message = Message.builder()
                .senderId(dto.getSenderId())
                .recipientId(dto.getRecipientId())
                .content(dto.getContent())
                .sentAt(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .build();

        Message saved = messageRepository.save(message);
        return mapToDTO(saved);
    }

    public List<MessageDTO> getChatHistory(Integer u1, Integer u2) {
        return messageRepository.findChatHistory(u1, u2)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MessageDTO mapToDTO(Message m) {
        MessageDTO dto = new MessageDTO();
        dto.setId(m.getId());
        dto.setSenderId(m.getSenderId());
        dto.setRecipientId(m.getRecipientId());
        dto.setContent(m.getContent());
        dto.setSentAt(m.getSentAt());
        return dto;
    }

    public List<ChatSummaryDTO> getChatSummaries(Integer currentUserId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().orOperator(
                        Criteria.where("senderId").is(currentUserId),
                        Criteria.where("recipientId").is(currentUserId)
                )),
                Aggregation.sort(Sort.Direction.DESC, "sentAt"),
                Aggregation.project("content", "sentAt", "status")
                        .and(ConditionalOperators.when(ComparisonOperators.Eq.valueOf("senderId").equalToValue(currentUserId))
                                .thenValueOf("recipientId")
                                .otherwiseValueOf("senderId"))
                        .as("interlocutorId"),
                Aggregation.group("interlocutorId")
                        .first("content").as("lastMessage")
                        .first("sentAt").as("lastMessageTime")
                        .first("status").as("status"),
                Aggregation.sort(Sort.Direction.DESC, "lastMessageTime")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "messages", Document.class);
        List<Document> mappedResults = results.getMappedResults();

        List<Integer> interlocutorIds = mappedResults.stream()
                .map(doc -> doc.get("_id"))
                .filter(java.util.Objects::nonNull)
                .map(id -> Integer.parseInt(id.toString()))
                .collect(Collectors.toList());

        Map<Integer, User> userMap = userRepository.findAllById(interlocutorIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return mappedResults.stream()
                .map(doc -> {
                    Object idObj = doc.get("_id");
                    if (idObj == null) return null;

                    Integer interlocutorId = Integer.parseInt(idObj.toString());
                    User user = userMap.get(interlocutorId);

                    String fullName = "Unknown User";
                    String avatar = null;

                    if (user != null && user.getProfile() != null) {
                        fullName = user.getProfile().getFirstName() + " " + user.getProfile().getLastName();
                        avatar = user.getProfile().getProfilePhoto();
                    }

                    return ChatSummaryDTO.builder()
                            .chatId(interlocutorId.toString())
                            .interlocutorId(interlocutorId)
                            .interlocutorName(fullName)
                            .avatarUrl(avatar)
                            .lastMessage(doc.getString("lastMessage"))
                            .lastMessageTime(doc.getDate("lastMessageTime"))
                            .unreadCount(0)
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}