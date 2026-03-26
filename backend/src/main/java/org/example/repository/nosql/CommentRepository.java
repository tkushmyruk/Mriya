package org.example.repository.nosql;

import org.example.domain.nosql.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findAllByPostIdOrderByCreateDateAsc(String postId);

    void deleteAllByPostId(String postId);
}