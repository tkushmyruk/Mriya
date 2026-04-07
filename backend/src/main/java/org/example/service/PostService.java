package org.example.service;

import lombok.AllArgsConstructor;
import org.example.domain.nosql.OwnerType;
import org.example.domain.nosql.Post;
import org.example.domain.nosql.PostStatus;
import org.example.domain.sql.User;
import org.example.dto.PostCreateRequest;
import org.example.repository.nosql.PostRepository;
import org.example.repository.sql.UserRepository;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final VectorStore vectorStore;
    private final NotificationService notificationService;
    // private final KafkaTemplate<String, PostEvent> kafkaTemplate;

    public Post createPost(PostCreateRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).get();
        Post post = new Post();
        post.setAuthorId(user.getId());
        post.setOwnerId(request.getOwnerId());
        post.setOwnerType(request.getOwnerType());
        post.setContent(request.getContent());
        post.setAuthorFirstName(user.getProfile().getFirstName());
        post.setAuthorLastName(user.getProfile().getLastName());
//        post.setTags(request.getTags());
        post.setPostStatus(PostStatus.PUBLISHED);
        post.setCreatedDate(LocalDateTime.now());
        Post savedPost = postRepository.save(post);

        // sendToVectorIndexing(savedPost);

        Document document = new Document(
                savedPost.getContent(),
                Map.of("postId", savedPost.getId())
        );

        vectorStore.add(List.of(document));

        return savedPost;
    }

    public List<Post> getPostsByOwner(Long ownerId, OwnerType ownerType) {
        return postRepository.findByOwnerIdAndOwnerTypeOrderByCreatedDateDesc(ownerId, ownerType);
    }

    public void incrementCommentsCount(String postId) {
        Query query = new Query(Criteria.where("id").is(postId));
        Update update = new Update().inc("commentsCount", 1);
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    public Post toggleLike(String postId, String email) {
        User user = userRepository.findByEmail(email).get();
        long userId = user.getId();
        Query query = new Query(Criteria.where("id").is(postId));
        Post post = mongoTemplate.findOne(query, Post.class);

        if (post == null) throw new RuntimeException("Post not found");

        Update update = new Update();
        boolean isLikedNow = false;

        if (post.getLikedBy() != null && post.getLikedBy().contains(userId)) {
            update.pull("likedBy", userId);
            update.inc("likesCount", -1);
        } else {
            update.push("likedBy", userId);
            update.inc("likesCount", 1);
            isLikedNow = true;
        }

        if (isLikedNow && post.getAuthorId() != userId) {
            notificationService.sendNotification(
                    post.getAuthorId(), userId
            );

        }

        mongoTemplate.updateFirst(query, update, Post.class);
        return mongoTemplate.findOne(query, Post.class);
    }

    public List<String> findLastLikedContentListed(String userEmail) {
        Pageable topFive = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDate"));

        Long userId = userRepository.findByEmail(userEmail).get().getId();
        List<Post> recentLikes = postRepository.findRecentLikes(userId, topFive);

        return recentLikes.stream().map(post -> post.getContent()).toList();
    }
}