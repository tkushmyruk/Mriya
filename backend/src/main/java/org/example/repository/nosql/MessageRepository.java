package org.example.repository.nosql;

import org.example.domain.nosql.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    @Query("{ $or: [ " +
            "{ 'senderId': ?0, 'recipientId': ?1 }, " +
            "{ 'senderId': ?1, 'recipientId': ?0 } " +
            "] }")
    List<Message> findChatHistory(Long u1, Long u2);

    Message findFirstBySenderIdAndRecipientIdOrderBySentAtDesc(Long senderId, Long recipientId);
}