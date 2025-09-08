package com.mtcoding.minigram.integre;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CommentsControllerTest extends MyRestDoc {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    private String accessToken;

    @BeforeEach
    void setUp() {

        User user = User.builder().id(2).username("ssar").roles("USER").build();
        accessToken = JwtUtil.create(user);
    }


    @Test
    @DisplayName("댓글 목록 조회 - OK (루트 배열, 대댓글 포함)")
    void findAll_ok() throws Exception {
        int postId = 18; // 더미에 존재하는 ID

        ResultActions actions = mvc.perform(
                get("/s/api/posts/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + accessToken)

        );

//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.items").isArray())
                .andExpect(jsonPath("$.body.items[0].commentId").value(1))
                .andExpect(jsonPath("$.body.items[0].user.userId").value(2))
                .andExpect(jsonPath("$.body.items[0].owner").value(true))
                .andExpect(jsonPath("$.body.items[0].postAuthor").value(false))
                // 좋아요 (시드: (1,3),(1,4),(1,5) → 3개, viewer=2는 좋아요 안함)
                .andExpect(jsonPath("$.body.items[0].likes.count").value(3))
                .andExpect(jsonPath("$.body.items[0].likes.isLiked").value(false))
                // createdAt은 값 패턴으로 검증
                .andExpect(jsonPath("$.body.items[0].createdAt",
                        matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")))

                // 첫 부모의 자식 댓글들(정렬: id asc → [6, 9])
                .andExpect(jsonPath("$.body.items[0].children", hasSize(2)))
                .andExpect(jsonPath("$.body.items[0].children[0].commentId").value(6))
                .andExpect(jsonPath("$.body.items[0].children[0].user.userId").value(8)) // luna
                .andExpect(jsonPath("$.body.items[0].children[0].postAuthor").value(true));

        actions.andDo(document);
    }
}
