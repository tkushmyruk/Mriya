package org.example.service;

import lombok.AllArgsConstructor;
import org.example.domain.nosql.OwnerType;
import org.example.domain.nosql.Post;
import org.example.domain.nosql.PostStatus;
import org.example.dto.PostCreateRequest;
import org.example.repository.nosql.PostRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
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
}