package org.example.service;

import lombok.AllArgsConstructor;
import org.example.domain.nosql.OwnerType;
import org.example.domain.nosql.Post;
import org.example.domain.nosql.PostStatus;
import org.example.dto.PostCreateRequest;
import org.example.repository.nosql.PostRepository;
import org.example.repository.sql.UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    // private final KafkaTemplate<String, PostEvent> kafkaTemplate; // Додамо пізніше

    public Post createPost(PostCreateRequest request) {
        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setOwnerId(request.getOwnerId());
        post.setOwnerType(request.getOwnerType());
        post.setContent(request.getContent());
//        post.setTags(request.getTags());
        post.setPostStatus(PostStatus.PUBLISHED);
        post.setCreatedDate(LocalDateTime.now());
        Post savedPost = postRepository.save(post);

        // TODO: Відправити подію в Kafka для Qdrant (генерація ембедінга)
        // sendToVectorIndexing(savedPost);

        return savedPost;
    }

    public List<Post> getPostsByOwner(Long ownerId, OwnerType ownerType) {
        return postRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
    }

    public void incrementCommentsCount(String postId) {
        Query query = new Query(Criteria.where("id").is(postId));
        Update update = new Update().inc("commentsCount", 1); // Збільшує на 1
        mongoTemplate.updateFirst(query, update, Post.class);
    }

    public Post toggleLike(String postId, String email) {
        int userId = userRepository.findByEmail(email).get().getId();
        Query query = new Query(Criteria.where("id").is(postId));
        Post post = mongoTemplate.findOne(query, Post.class);

        if (post == null) throw new RuntimeException("Post not found");

        Update update = new Update();

        if (post.getLikedBy() != null && post.getLikedBy().contains(userId)) {
            update.pull("likedBy", userId);
            update.inc("likesCount", -1);
        } else {
            update.push("likedBy", userId);
            update.inc("likesCount", 1);
        }

        mongoTemplate.updateFirst(query, update, Post.class);
        return mongoTemplate.findOne(query, Post.class);
    }
}