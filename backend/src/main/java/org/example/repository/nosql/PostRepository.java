package org.example.repository.nosql;

import org.example.domain.nosql.OwnerType;
import org.example.domain.nosql.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    List<Post> findByAuthorId(Long authorId);

    List<Post> findByTagsIn(List<String> tags);

    List<Post> findByOwnerIdAndOwnerTypeOrderByCreatedDateDesc(Long ownerId, OwnerType ownerType);

    List<Post> findByLikedByContainingOrderByCreatedDateDesc(String userEmail, Pageable pageable);

    @Query("{ 'likedBy': ?0 }")
    List<Post> findRecentLikes(Long userId, Pageable pageable);
}