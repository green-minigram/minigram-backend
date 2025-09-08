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
public class NotificationsControllerTest extends MyRestDoc {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

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
                get("/s/api/notifications")
                        .param("userId", "8")
                        .header("Authorization", "Bearer " + accessToken) // JWT 필터 대비
        );

        // eye
//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.items").isArray())
                .andExpect(jsonPath("$.body.items", hasSize(5)))
                // 공통 필드
                // ====== item[0] : id=12 FOLLOW (sender=9 zero, UNREAD) ======
                .andExpect(jsonPath("$.body.items[0].id").value(12))
                .andExpect(jsonPath("$.body.items[0].type").value("FOLLOW"))
                .andExpect(jsonPath("$.body.items[0].read").value(false))
                .andExpect(jsonPath("$.body.items[0].user.userId").value(9))
                .andExpect(jsonPath("$.body.items[0].user.username").value("zero"))
                .andExpect(jsonPath("$.body.items[0].createdAt",
                        matchesPattern("\\d{4}-\\d{2}-\\d{2}T.*")))
                .andExpect(jsonPath("$.body.items[0].post").doesNotExist())

                // ====== item[1] : id=7 COMMENT (sender=3 cos, READ) ======
                .andExpect(jsonPath("$.body.items[1].id").value(7))
                .andExpect(jsonPath("$.body.items[1].type").value("COMMENT"))
                .andExpect(jsonPath("$.body.items[1].read").value(true))
                .andExpect(jsonPath("$.body.items[1].user.userId").value(3))
                .andExpect(jsonPath("$.body.items[1].user.username").value("cos"))
                .andExpect(jsonPath("$.body.items[1].post.postId").value(18))
                .andExpect(jsonPath("$.body.items[1].post.commentId").value(2))
                .andExpect(jsonPath("$.body.items[1].post.postImage").isString())
                .andExpect(jsonPath("$.body.items[1].post.comment").value("비하인드도 기대됩니다!"))

                // ====== item[2] : id=6 COMMENT (sender=2 ssar, UNREAD) ======
                .andExpect(jsonPath("$.body.items[2].id").value(6))
                .andExpect(jsonPath("$.body.items[2].type").value("COMMENT"))
                .andExpect(jsonPath("$.body.items[2].read").value(false))
                .andExpect(jsonPath("$.body.items[2].user.userId").value(2))
                .andExpect(jsonPath("$.body.items[2].user.username").value("ssar"))
                .andExpect(jsonPath("$.body.items[2].post.postId").value(18))
                .andExpect(jsonPath("$.body.items[2].post.commentId").value(1))
                .andExpect(jsonPath("$.body.items[2].post.comment").value("첫 댓글! 영상 너무 재밌어요 🙌"))

                // ====== item[3] : id=5 POST_LIKE (sender=10 rain, READ) ======
                .andExpect(jsonPath("$.body.items[3].id").value(5))
                .andExpect(jsonPath("$.body.items[3].type").value("POST_LIKE"))
                .andExpect(jsonPath("$.body.items[3].read").value(true))
                .andExpect(jsonPath("$.body.items[3].user.userId").value(10))
                .andExpect(jsonPath("$.body.items[3].user.username").value("rain"))
                .andExpect(jsonPath("$.body.items[3].post.postId").value(18))
                .andExpect(jsonPath("$.body.items[3].post.postImage").isString())

                // ====== item[4] : id=4 POST_LIKE (sender=7 neo, UNREAD) ======
                .andExpect(jsonPath("$.body.items[4].id").value(4))
                .andExpect(jsonPath("$.body.items[4].type").value("POST_LIKE"))
                .andExpect(jsonPath("$.body.items[4].read").value(false))
                .andExpect(jsonPath("$.body.items[4].user.userId").value(7))
                .andExpect(jsonPath("$.body.items[4].user.username").value("neo"))
                .andExpect(jsonPath("$.body.items[4].post.postId").value(18))
                .andExpect(jsonPath("$.body.items[4].post.postImage").isString());

        actions.andDo(document);
    }
}
