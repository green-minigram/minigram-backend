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
class CommentLikesControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;
    String accessToken;

    @BeforeEach
    void setUp() {
        User u = User.builder().id(2).username("ssar").roles("USER").build();
        accessToken = JwtUtil.create(u);
    }

    @Test
    @DisplayName("댓글 좋아요 - OK (응답 출력)")
    void like_ok() throws Exception {
        int commentId = 42; // seed에 존재한다고 가정

        // 초기화: 취소 한 번
        mvc.perform(delete("/s/api/comments/{cid}/likes", commentId)
                .header("Authorization", "Bearer " + accessToken));

        ResultActions actions = mvc.perform(
                        post("/s/api/comments/{cid}/likes", commentId)
                                .header("Authorization", "Bearer " + accessToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.body.isLiked").value(true))
                .andExpect(jsonPath("$.body.count").isNumber());

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(">>> comment like RAW: " + responseBody);
        String pretty = om.writerWithDefaultPrettyPrinter()
                .writeValueAsString(om.readTree(responseBody));
        System.out.println(">>> comment like PRETTY:\n" + pretty);
    }

    @Test
    @DisplayName("댓글 좋아요 취소 - OK (멱등)")
    void unlike_ok() throws Exception {
        int commentId = 42;

        mvc.perform(post("/s/api/comments/{cid}/likes", commentId)
                .header("Authorization", "Bearer " + accessToken));

        ResultActions actions = mvc.perform(
                        delete("/s/api/comments/{cid}/likes", commentId)
                                .header("Authorization", "Bearer " + accessToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.body.isLiked").value(false))
                .andExpect(jsonPath("$.body.count").isNumber());

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(">>> comment unlike RAW: " + responseBody);
    }
}