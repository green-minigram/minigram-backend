package com.mtcoding.minigram.stories;

import com.mtcoding.minigram._core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StoriesController {
    private final StoryService storyService;

//    // 로그인 유저의 스토리 목록 (최근 5개)
//    @GetMapping("/s/api/users/me/stories")
//    public ResponseEntity<?> findAll(@AuthenticationPrincipal User user) {
//        StoryResponse.ListDTO respDTO = storyService.findAll(user.getId());
//        return Resp.ok(respDTO);
//    }

    // 스토리 상세
    @GetMapping("/s/api/stories/{storyId}")
    public ResponseEntity<?> findByStoryId(@PathVariable Integer storyId) {
        StoryResponse.DetailDTO respDTO = storyService.findByStoryId(storyId);
        return Resp.ok(respDTO);
    }

//    // 특정 유저의 스토리 목록 (최근 5개)
//    @GetMapping("/s/api/users/{userId}/stories")
//    public ResponseEntity<?> findAllByUserId(@PathVariable Integer userId) {
//        StoryResponse.ListDTO respDTO = storyService.findAllByUserId(userId);
//        return Resp.ok(respDTO);
//    }
}
