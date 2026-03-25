package org.example.domain.nosql;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Getter
@Setter
public class Comment {
    @Id
    private String id;

    @Indexed
    private String postId;

    private Long authorId;

    private String text;

    private String parentCommentId;

    private LocalDateTime createDate = LocalDateTime.now();
}
