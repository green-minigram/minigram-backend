package com.mtcoding.minigram.integre.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PostsControllerTest extends MyRestDoc {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    private User mockUser;

    private String accessToken;

    @BeforeEach
    void setUpSecurityContext() {
        mockUser = User.builder().id(1).username("ssar123").role(Role.USER).build();
        accessToken = JwtUtil.create(mockUser);

        var auth = new UsernamePasswordAuthenticationToken(
                mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("게시글 단건 조회 - OK")
    void find_ok() throws Exception {

        int postId = 18;

        ResultActions actions = mvc.perform(
                get("/api/posts/{postId}", postId)
                        .header("Authorization", "Bearer " + accessToken) // 필터 파싱에 맞추어 접두어 유지/제거
                        .param("viewerId", String.valueOf(mockUser.getId()))
                        .sessionAttr("sessionUser", User.builder()
                                .id(1).username("ssar123").role(Role.USER).build())
        );

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(18))
                .andExpect(jsonPath("$.author.userId").value(8))
                .andExpect(jsonPath("$.author.username").value("luna"))
                .andExpect(jsonPath("$.content").value("브이로그: 하루 일상 ☀️"))
                .andExpect(jsonPath("$.images", hasSize(10)))     // 정확히 10개면 이렇게
                // .andExpect(jsonPath("$.images", hasSize(greaterThan(0)))) // 개수 변동 가능하면 이렇게
                .andExpect(jsonPath("$.images[0].id").isNumber())
                .andExpect(jsonPath("$.images[0].url").isString())
                .andExpect(jsonPath("$.createdAt").value(nullValue()))       // null 확인
                .andExpect(jsonPath("$.likes.count").isNumber())
                .andExpect(jsonPath("$.likes.isLiked").isBoolean())
                .andExpect(jsonPath("$.commentCount").value(15))
                .andExpect(jsonPath("$.isFollowing").isBoolean())
                .andExpect(jsonPath("$.isOwner").isBoolean());

        actions.andDo(document);
    }
}