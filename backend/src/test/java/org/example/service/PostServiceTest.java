package org.example.service;

import org.example.domain.nosql.OwnerType;
import org.example.domain.nosql.Post;
import org.example.domain.sql.Profile;
import org.example.domain.sql.Role;
import org.example.domain.sql.User;
import org.example.dto.PostCreateRequest;
import org.example.repository.nosql.PostRepository;
import org.example.repository.sql.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VectorStore vectorStore;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private PostService postService;

    private PostCreateRequest postCreateRequest;
    private User user;
    private Post post;
    private String TEST_EMAIL = "test@gmail.com";


    @BeforeEach
    void setUp() {
        postCreateRequest = PostCreateRequest.builder()
                .ownerId(1L)
                .ownerType(OwnerType.PROFILE)
                .content("Content")
                .tags(List.of("hashtag"))
                .build();

        Profile profile = Profile.builder()
                .id(1L).userId(2L).firstName("VASYA").lastName("PUPKIN").profilePhoto("http://fake-link/").createdAt(new Timestamp(System.currentTimeMillis())).build();

        user = User.builder()
                .id(2L)
                .phone("123123123")
                .email(TEST_EMAIL)
                .profile(profile)
                .password("test")
                .createDate(new Timestamp(System.currentTimeMillis()))
                .role(Role.USER)
                .build();

        post = Post.builder().id("1231231231231231").content("content").build();

    }

    @Test
    void create_post_test() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        postService.createPost(postCreateRequest, TEST_EMAIL);


        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(vectorStore, times(1)).add(anyList());
    }


}
