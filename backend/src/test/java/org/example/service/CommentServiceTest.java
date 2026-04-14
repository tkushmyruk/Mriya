package org.example.service;

import org.example.domain.nosql.Comment;
import org.example.domain.sql.Profile;
import org.example.domain.sql.User;
import org.example.dto.CreateCommentRequest;
import org.example.repository.nosql.CommentRepository;
import org.example.repository.sql.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostService postService;
    @Mock
    private ProfileService profileService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void addComment_ShouldSaveCommentAndIncrementCount() {
        String email = "test@example.com";
        Long userId = 1L;
        String postId = "post-123";
        CreateCommentRequest request = new CreateCommentRequest(postId, "Nice post!");

        User user = new User();
        user.setId(userId);

        Profile profile = Profile.builder()
                .firstName("Artem")
                .lastName("Test")
                .build();

        Comment savedComment = Comment.builder()
                .id("comment-999")
                .text(request.getContent())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(profileService.getProfileByUserId(userId)).thenReturn(profile);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        Comment result = commentService.addComment(email, request);

        assertNotNull(result);
        assertEquals("comment-999", result.getId());

        verify(commentRepository).save(argThat(comment ->
                comment.getAuthorName().equals("Artem Test") &&
                        comment.getPostId().equals(postId)
        ));

        verify(postService, times(1)).incrementCommentsCount(postId);

        verify(postService, atLeast(1)).incrementCommentsCount(postId);
    }

}
