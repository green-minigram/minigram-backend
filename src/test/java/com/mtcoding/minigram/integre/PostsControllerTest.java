package com.mtcoding.minigram.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.posts.PostRequest;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PostsControllerTest extends MyRestDoc {


    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    private String accessToken;

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전에 실행할 코드
        User user = User.builder().id(2).username("ssar").roles("USER").build();
        accessToken = JwtUtil.create(user);
    }

    @Test
    @DisplayName("게시글 단건 조회 - OK")
    void find_ok() throws Exception {

        int postId = 18;

        ResultActions actions = mvc.perform(
                get("/s/api/posts/{postId}", postId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
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

    @Test
    @DisplayName("게시글 작성 - OK (JSON)")
    void create_ok() throws Exception {
        var req = new PostRequest.CreateDTO();
        req.setContent("주말 바다 🌊");
        req.setImageUrls(List.of(
                "https://picsum.photos/seed/a/800",
                "https://picsum.photos/seed/b/800"
        ));

        ResultActions actions = mvc.perform(post("/s/api/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.postId").isNumber())
                .andExpect(jsonPath("$.body.author.userId").value(2))
                .andExpect(jsonPath("$.body.author.username").value("ssar"))
                .andExpect(jsonPath("$.body.author.isOwner").value(true))
                .andExpect(jsonPath("$.body.images.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.body.content").value("주말 바다 🌊"))
                .andExpect(jsonPath("$.body.likes.count").value(0))
                .andExpect(jsonPath("$.body.likes.isLiked").value(false))
                .andExpect(jsonPath("$.body.commentCount").value(0))
                .andExpect(jsonPath("$.body.postedAt").isString())
                .andExpect(jsonPath("$.body.isReported").value(false));

        actions.andDo(document);
    }

    // ===============================
// 게시글 작성 - 이미지 없음 -> 400 (유효성 실패)
// ===============================
    @Test
    @DisplayName("게시글 작성 - 실패(이미지 없음) - 400")
    void create_fail_no_images() throws Exception {
        var req = new PostRequest.CreateDTO();
        req.setContent("이미지 없이 작성");
        req.setImageUrls(java.util.List.of());

        // when
        ResultActions actions = mvc.perform(post("/s/api/posts")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON)
        );

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg", containsString("이미지")))
                .andExpect(jsonPath("$.body").doesNotExist());

        actions.andDo(document);
    }
}