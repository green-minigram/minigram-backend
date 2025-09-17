package com.mtcoding.minigram.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.posts.PostRequest;
import com.mtcoding.minigram.users.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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
    void find_test() throws Exception {

        int postId = 18;

        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.get("/s/api/posts/{postId}", postId)
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.postId").value(18))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.author.userId").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.author.username").value("luna"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.author.profileImageUrl",
                        Matchers.anyOf(Matchers.nullValue(), Matchers.matchesPattern("^https?://.+"))
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.author.isFollowing").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.author.isOwner").value(false))

                .andExpect(MockMvcResultMatchers.jsonPath("$.body.images", Matchers.hasSize(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.images[0].id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.images[0].url").isString())

                .andExpect(MockMvcResultMatchers.jsonPath("$.body.content").value("브이로그: 하루 일상 ☀️"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.likes.count").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.likes.isLiked").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.commentCount").value(15))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.postedAt").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.isReported").value(true));

        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
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
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.postId").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.images", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.images[0].id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.images[0].url").value("https://picsum.photos/seed/a/800"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.images[1].url").value("https://picsum.photos/seed/b/800"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.content").value("주말 바다 🌊"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.postedAt").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.updatedAt").isString());

        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
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
        actions.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg", Matchers.containsString("이미지")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body").doesNotExist());

        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
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

        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.postId").value(postId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.deleted").value(true));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

        // 2) 삭제 후 상세 조회 → 404
        mvc.perform(
                        MockMvcRequestBuilders.get("/s/api/posts/{postId}", postId)
                                .header("Authorization", "Bearer " + accessToken)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404));

        // 3) 다시 삭제
        mvc.perform(
                        MockMvcRequestBuilders.delete("/s/api/posts/{postId}", postId)
                                .header("Authorization", accessToken)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.postId").value(postId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.deleted").value(true));
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
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.current").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.size").value(12));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalCount").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalPage").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.prev").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.next").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFirst").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isLast").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postId").value(21));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postImageUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].content").value("오늘의 추천 음악 \uD83C\uDFB6"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    void update_test() throws Exception {
        Integer postId = 3;

        PostRequest.UpdateDTO reqDTO = new PostRequest.UpdateDTO();
        reqDTO.setContent("수정된 본문");

        String requestBody = om.writeValueAsString(reqDTO);
        // System.out.println(requestBody);

        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/posts/{postId}", postId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
        );

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postId").value(3));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.content").value("수정된 본문"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.updatedAt", Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?$")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.images[0].id").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.images[0].url").isString());
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}