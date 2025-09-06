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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class NotificationsControllerTest extends MyRestDoc {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    private User mockUser;

    private MockHttpSession session;
    private User viewer;

    private String accessToken;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(8).username("luna").roles("USER").build();
        accessToken = JwtUtil.create(user);
    }

    @Test
    @DisplayName("알림 목록 조회 - OK (루트 items 배열, 타입별 타겟 선택 포함)")
    void findAll_ok() throws Exception {
        // when
        ResultActions actions = mvc.perform(
                get("s/api/notifications")
                        .header("Authorization", "Bearer " + accessToken) // JWT 필터 대비                 // RoleFilter 대비
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.items").isArray())
                .andExpect(jsonPath("$.body.items", hasSize(greaterThan(0))))
                // 공통 필드
                .andExpect(jsonPath("$.body.items[0].id").isNumber())
                .andExpect(jsonPath("$.body.items[0].type").isString())
                .andExpect(jsonPath("$.body.items[0].createdAt").isString())
                .andExpect(jsonPath("$.body.items[0].read").isBoolean())
                .andExpect(jsonPath("$.body.items[0].actor.userId").isNumber())
                .andExpect(jsonPath("$.body.items[0].actor.username").isString())
                .andExpect(jsonPath("$.body.items[0].message").isString())
                // COMMENT 타입이 최소 1개 존재하고, target.commentId 포함
                .andExpect(jsonPath("$.body.items[?(@.type=='COMMENT')].target.commentId", not(empty())))
                // POST_LIKE 타입이 최소 1개 존재하고, target.postId 포함
                .andExpect(jsonPath("$.body.items[?(@.type=='POST_LIKE')].target.postId", not(empty())));
        // FOLLOW 타입은 target이 없을 수 있으므로 강제 검증 X

        actions.andDo(document);
    }
}
