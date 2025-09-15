package com.mtcoding.minigram.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.posts.PostRequest;
import com.mtcoding.minigram.users.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    @DisplayName("게시글 단건 조회 - OK (일반 게시글)")
    void find_test() throws Exception {

        int postId = 18;

        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.get("/s/api/posts/{postId}", postId)
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.postId").value(18))
                .andExpect(jsonPath("$.body.author.userId").value(8))
                .andExpect(jsonPath("$.body.author.username").value("luna"))
                .andExpect(jsonPath("$.body.author.profileImageUrl",
                        Matchers.anyOf(Matchers.nullValue(), Matchers.matchesPattern("^https?://.+"))
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
    void create_test() throws Exception {
        var req = new PostRequest.CreateDTO();
        req.setContent("주말 바다 🌊");
        req.setImageUrls(List.of(
                "https://picsum.photos/seed/a/800",
                "https://picsum.photos/seed/b/800"
        ));

        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/s/api/posts")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON)
        );

//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        // then
        actions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.postId").isNumber())
                .andExpect(jsonPath("$.body.userId").value(2))
                .andExpect(jsonPath("$.body.images", hasSize(2)))
                .andExpect(jsonPath("$.body.images[0].id").isNumber())
                .andExpect(jsonPath("$.body.images[0].url").value("https://picsum.photos/seed/a/800"))
                .andExpect(jsonPath("$.body.images[1].url").value("https://picsum.photos/seed/b/800"))
                .andExpect(jsonPath("$.body.content").value("주말 바다 🌊"))
                .andExpect(jsonPath("$.body.postedAt").isString())
                .andExpect(jsonPath("$.body.updatedAt").isString());

        actions.andDo(document);
    }

    @Test
    @DisplayName("게시글 작성 - 실패(이미지 없음) - 400")
    void create_fail_test() throws Exception {
        var req = new PostRequest.CreateDTO();
        req.setContent("이미지 없이 작성");
        req.setImageUrls(java.util.List.of());

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/s/api/posts")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON)
        );

//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg", Matchers.containsString("이미지")))
                .andExpect(jsonPath("$.body").doesNotExist());

        actions.andDo(document);
    }

    @Test
    @DisplayName("게시글 삭제 - OK (소유자, 멱등 + 이후 조회 404)")
    void delete_test() throws Exception {
        int postId = 3; // ssar(id=2)가 소유한 게시글이라고 가정

        // 1) 최초 삭제
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.delete("/s/api/posts/{postId}", postId)
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.postId").value(postId))
                .andExpect(jsonPath("$.body.deleted").value(true))
                .andDo(document);

        // 2) 삭제 후 상세 조회 → 404
        mvc.perform(
                        MockMvcRequestBuilders.get("/s/api/posts/{postId}", postId)
                                .header("Authorization", "Bearer " + accessToken)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        // 3) 다시 삭제
        mvc.perform(
                        MockMvcRequestBuilders.delete("/s/api/posts/{postId}", postId)
                                .header("Authorization", accessToken)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.postId").value(postId))
                .andExpect(jsonPath("$.body.deleted").value(true));
    }

    @Test
    public void search_test() throws Exception {
        // given
        Integer page = 0;
        String keyword = "오늘";

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/search/posts")
                        .param("page", page.toString())
                        .param("keyword", keyword)
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.current").value(0));
        actions.andExpect(jsonPath("$.body.size").value(12));
        actions.andExpect(jsonPath("$.body.totalCount").value(4));
        actions.andExpect(jsonPath("$.body.totalPage").value(1));
        actions.andExpect(jsonPath("$.body.prev").value(0));
        actions.andExpect(jsonPath("$.body.next").value(0));
        actions.andExpect(jsonPath("$.body.isFirst").value(true));
        actions.andExpect(jsonPath("$.body.isLast").value(true));
        actions.andExpect(jsonPath("$.body.postList").isArray());
        actions.andExpect(jsonPath("$.body.postList[0].postId").value(21));
        actions.andExpect(jsonPath("$.body.postList[0].postImageUrl").isString());
        actions.andExpect(jsonPath("$.body.postList[0].content").value("오늘의 추천 음악 \uD83C\uDFB6"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    void update_ok() throws Exception {
        String body = """
                {
                  "content": "수정된 본문",
                  "imageUrls": ["https://picsum.photos/seed/u1/800","https://picsum.photos/seed/u2/800"]
                }
                """;

        ResultActions actions = mvc.perform(put("/s/api/posts/{id}", 3)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.postId").value(3))
                .andExpect(jsonPath("$.body.content").value("수정된 본문"))
                .andExpect(jsonPath("$.body.images", hasSize(2)))
                .andExpect(jsonPath("$.body.updatedAt").isString());
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

}