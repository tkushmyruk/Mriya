package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.nosql.Comment;
import org.example.domain.sql.Profile;
import org.example.dto.CreateCommentRequest;
import org.example.repository.nosql.CommentRepository;
import org.example.repository.sql.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final ProfileService profileService;
    private final UserRepository userRepository;

    public Comment addComment(String userEmail, CreateCommentRequest request) {
        Long currentUserId = userRepository.findByEmail(userEmail).get().getId();
        Profile profile = profileService.getProfileByUserId(currentUserId);

        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .authorId(currentUserId)
                .authorName(profile.getFirstName() + " " + profile.getLastName())
                .text(request.getContent())
                .createDate(LocalDateTime.now())
                .build();

        postService.incrementCommentsCount(request.getPostId());

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPostId(String postId) {
        return commentRepository.findAllByPostIdOrderByCreateDateAsc(postId);
    }
}
