package org.example.domain.nosql;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Document(collection = "posts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(def = "{'ownerId': 1, 'ownerType': 1}")
public class Post {
    @Id
    private String id;

    @Indexed
    private Long authorId;

    private String authorFirstName;
    private String authorLastName;

    private String content;

    @Indexed
    private PostStatus postStatus;

    private List<String> mediaUrls;
    private Set<Long> likedBy;
    private int likeCount;

    private List<String> tags;
    private Long ownerId;
    private OwnerType ownerType;

    @Builder.Default
    private long likesCount = 0;

    @Builder.Default
    private long commentsCount = 0;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}