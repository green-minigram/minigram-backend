package com.mtcoding.minigram.integre;

import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.enums.Role;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PostsControllerTest extends MyRestDoc {


    @Autowired
    private MockMvc mvc;

    private String accessToken;

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전에 실행할 코드
        User user = User.builder().id(2).username("ssar").role(Role.USER).build();
        accessToken = JwtUtil.create(user);
    }

    @Test
    @DisplayName("게시글 단건 조회 - OK")
    void find_ok() throws Exception {

        int postId = 18;

        ResultActions actions = mvc.perform(
                get("/s/api/posts/{postId}", postId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON)
        );

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.postId").value(18))
                .andExpect(jsonPath("$.body.author.userId").value(8))
                .andExpect(jsonPath("$.body.author.username").value("luna"))
                .andExpect(jsonPath("$.body.author.profileImageUrl",
                        anyOf(nullValue(), matchesPattern("^https?://.+"))
                ))
                .andExpect(jsonPath("$.body.author.isFollowing").value(true))
                .andExpect(jsonPath("$.body.author.isOwner").value(false))

                .andExpect(jsonPath("$.body.images", hasSize(10)))
                .andExpect(jsonPath("$.body.images[0].id").isNumber())
                .andExpect(jsonPath("$.body.images[0].url").isString())

                .andExpect(jsonPath("$.body.content").value("브이로그: 하루 일상 ☀️"))
                .andExpect(jsonPath("$.body.likes.count").value(8))
                .andExpect(jsonPath("$.body.likes.isLiked").value(true))
                .andExpect(jsonPath("$.body.commentCount").value(15))
                .andExpect(jsonPath("$.body.postedAt").isString())
                .andExpect(jsonPath("$.body.isReported").value(true));


        actions.andDo(document);
    }
}