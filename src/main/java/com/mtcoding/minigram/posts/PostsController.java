package com.mtcoding.minigram.posts;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s/api/posts")
public class PostsController {
    private final PostService postService;
    private final HttpSession session;

    @GetMapping("/{postId}")
    public ResponseEntity<?> find(@PathVariable Integer postId, @AuthenticationPrincipal User user) {
        PostResponse.DetailDTO respDTO = postService.find(postId, user.getId());
        return Resp.ok(respDTO);
    }

    @PostMapping()
    public ResponseEntity<?> create(@AuthenticationPrincipal User user, @RequestBody PostRequest.CreateDTO reqDTO) {
        var respDTO = postService.create(reqDTO, user.getId());
        return Resp.ok(respDTO); // DetailDTO 반환
    }

}
