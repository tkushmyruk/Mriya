package org.example.domain.nosql;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Getter
@Setter
@Builder
public class Comment {
    @Id
    private String id;

    @Indexed
    private String postId;

    private Long authorId;

    private String authorName;

    private String text;

    private LocalDateTime createDate;
}
