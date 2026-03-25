package org.example.repository.nosql;

import org.example.domain.nosql.OwnerType;
import org.example.domain.nosql.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByAuthorId(Long authorId);
    List<Post> findByTagsIn(List<String> tags);
    List<Post> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);
}