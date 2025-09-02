package com.mtcoding.minigram.posts;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostsController {
    private final PostService postService;
    private final HttpSession session;


    @GetMapping("/api/posts/{postId}")
    public ResponseEntity<PostResponse.DetailDTO> find(
            @PathVariable Integer postId,
            @RequestParam(required = false) Integer viewerId
    ) {
        return ResponseEntity.ok(postService.find(postId, viewerId));
    }

}
