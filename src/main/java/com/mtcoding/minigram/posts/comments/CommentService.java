package com.mtcoding.minigram.posts.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// @Slf4j
// - Lombok이 자동으로 Logger 필드를 추가해주는 어노테이션
// - log.info()/debug()/error()
@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
}
