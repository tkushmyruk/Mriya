package org.example.domain.nosql;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "likes")
@CompoundIndex(def = "{'postId': 1, 'userId': 1}", unique = true)
public class Like {
    @Id
    private String id;
    private String postId;
    private Long userId;
}
