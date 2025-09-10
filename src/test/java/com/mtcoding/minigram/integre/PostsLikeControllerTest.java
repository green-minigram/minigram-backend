package com.mtcoding.minigram.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class PostsLikeControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    private String accessToken;

    @BeforeEach
    void setUp() {
        // 테스트 사용자: ssar(id=2)
        User user = User.builder().id(2).username("ssar").roles("USER").build();
        accessToken = JwtUtil.create(user);
    }

    @Test
    @DisplayName("게시글 좋아요 - OK (응답 바디 출력)")
    void like_ok_print() throws Exception {
        int postId = 18;

        // 초기화: 혹시 이미 좋아요 상태면 한 번 취소 (없어도 무시됨)
        mvc.perform(delete("/s/api/posts/{postId}/likes", postId)
                .header("Authorization", accessToken));

        // 요청
        ResultActions actions = mvc.perform(
                        post("/s/api/posts/{postId}/likes", postId)
                                .header("Authorization", accessToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.body.isLiked").value(true))
                .andExpect(jsonPath("$.body.count").isNumber());

        // 응답 바디 출력
//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 - OK (응답 바디 출력)")
    void unlike_ok_print() throws Exception {
        int postId = 18;

        // 선행: 좋아요 상태 만들기
        mvc.perform(post("/s/api/posts/{postId}/likes", postId)
                .header("Authorization", accessToken));

        // 요청
        ResultActions actions = mvc.perform(
                        delete("/s/api/posts/{postId}/likes", postId)
                                .header("Authorization", accessToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.body.isLiked").value(false))
                .andExpect(jsonPath("$.body.count").isNumber());

        // 응답 바디 출력
//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);
    }
}
