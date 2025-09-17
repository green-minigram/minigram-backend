package com.mtcoding.minigram.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AdvertisementsControllerTest extends MyRestDoc {


    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    private String adminToken;

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전에 실행할 코드
        User user = User.builder().id(1).username("minigram").roles("ADMIN").build();
        adminToken = JwtUtil.create(user);
    }


    @Test
    void create_test() throws Exception {
        // given
        String body = """
                {
                  "content": "📢 [광고] 가을 세일 최대 50%!",
                  "imageUrls": [
                    "https://picsum.photos/seed/ad_new_a/800/600",
                    "https://picsum.photos/seed/ad_new_b/800/600"
                  ],
                  "startAt": "2025-09-11T00:00:00",
                  "endAt":   "2025-09-18T23:59:59"
                }
                """;

        // when
        ResultActions actions = mvc.perform(
                post("/s/api/admin/advertisements")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        );

//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        // then (신규 postId/adId는 고정값이 아니므로 존재/양수만 체크)
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.adId").exists())
                .andExpect(jsonPath("$.body.postId", greaterThan(0)))
                .andExpect(jsonPath("$.body.userId").value(1))
                .andExpect(jsonPath("$.body.status").value("ACTIVE"))
                .andExpect(jsonPath("$.body.createdAt", matchesPattern("\\d{4}-\\d{2}-\\d{2}T.*")))
                .andExpect(jsonPath("$.body.updatedAt", matchesPattern("\\d{4}-\\d{2}-\\d{2}T.*")))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    void delete_ok() throws Exception {
        int adId = 1; // @MapsId → adId == postId

        // 1) 최초 삭제 (문서화 포함)
        ResultActions actions = mvc.perform(
                        delete("/s/api/admin/advertisements/{adId}", adId)
                                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.adId").value(1))
                .andExpect(jsonPath("$.body.deleted").value(true))
                .andDo(MockMvcResultHandlers.print()).andDo(document);

//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        // 2) 멱등: 다시 삭제해도 200/true
        mvc.perform(delete("/s/api/admin/advertisements/{adId}", adId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.deleted").value(true));

        // 3) 상세 재조회 → 404 (게시글도 DELETED 처리라 노출 금지)
        ResultActions detailActions = mvc.perform(get("/s/api/posts/{postId}", adId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

//        String detailBody = detailActions.andReturn().getResponse().getContentAsString();
//        System.out.println(detailBody);
    }
}

