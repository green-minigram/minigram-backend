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

import static org.hamcrest.Matchers.nullValue;
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
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].commentId").isNumber())
                .andExpect(jsonPath("$[0].user.userId").isNumber())
                .andExpect(jsonPath("$[0].content").isString())
                .andExpect(jsonPath("$[0].createdAt").isString())
                .andExpect(jsonPath("$[0].parentId").value(nullValue()))
                .andExpect(jsonPath("$[0].children").isArray())
                .andExpect(jsonPath("$[0].likes.count").isNumber())
                .andExpect(jsonPath("$[0].likes.isLiked").isBoolean())
                .andExpect(jsonPath("$[0].owner").isBoolean())
                .andExpect(jsonPath("$[0].postAuthor").isBoolean());

        // 대댓글 한 건 샘플 체크 (상황에 맞게 index 조정)
        actions.andExpect(jsonPath("$[0].children[0].commentId").isNumber())
                .andExpect(jsonPath("$[0].children[0].parentId").value(1));

        actions.andDo(document);
    }
}
