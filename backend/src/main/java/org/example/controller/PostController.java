package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.nosql.OwnerType;
import org.example.domain.nosql.Post;
import org.example.dto.PostCreateRequest;
import org.example.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Post create(@RequestBody PostCreateRequest request) {
        return postService.createPost(request);
    }
    @GetMapping("/{ownerType}/{ownerId}")
    public List<Post> getPosts(@PathVariable OwnerType ownerType, @PathVariable Long ownerId) {
        return postService.getPostsByOwner(ownerId, ownerType);
    }
}
