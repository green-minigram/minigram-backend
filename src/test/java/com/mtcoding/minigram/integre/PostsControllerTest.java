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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                get("/s/api/posts/{postId}", postId)
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
                .andExpect(jsonPath("$.body.isReported").value(true))
                .andExpect(jsonPath("$.body.isAd").value(false));


        actions.andDo(document);
    }

    @Test
    @DisplayName("게시글 단건 조회 - OK (광고 게시글: isFollowing 미노출)")
    void find_ad_post_test() throws Exception {
        // 시드에서 postId=1을 광고로 설정해둔다 (ACTIVE + 기간 유효)
        int postId = 1;

        ResultActions actions = mvc.perform(
                get("/s/api/posts/{postId}", postId)
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.postId").value(1))
                .andExpect(jsonPath("$.body.author.isFollowing").doesNotExist())

                .andExpect(jsonPath("$.body.author.userId").value(1))
                .andExpect(jsonPath("$.body.author.username").value("minigram"))
                .andExpect(jsonPath("$.body.author.profileImageUrl",
                        anyOf(nullValue(), matchesPattern("^https?://.+"))))
                .andExpect(jsonPath("$.body.author.isOwner").value(false))
                .andExpect(jsonPath("$.body.images", hasSize(2)))
                .andExpect(jsonPath("$.body.images[0].id").isNumber())
                .andExpect(jsonPath("$.body.images[0].url").isString())
                .andExpect(jsonPath("$.body.content",
                        containsString("[광고]")))
                .andExpect(jsonPath("$.body.likes.count").value(3))
                .andExpect(jsonPath("$.body.likes.isLiked").value(true))
                .andExpect(jsonPath("$.body.commentCount").value(5))
                .andExpect(jsonPath("$.body.postedAt",
                        matchesPattern("\\d{4}-\\d{2}-\\d{2}T.*")))
                .andExpect(jsonPath("$.body.isReported").value(false))
                .andExpect(jsonPath("$.body.isAd").value(true));  // ← 광고
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

        ResultActions actions = mvc.perform(post("/s/api/posts")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON));

//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        // then
        actions.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
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
        ResultActions actions = mvc.perform(post("/s/api/posts")
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
                .andExpect(jsonPath("$.msg", containsString("이미지")))
                .andExpect(jsonPath("$.body").doesNotExist());

        actions.andDo(document);
    }

    @Test
    @DisplayName("게시글 삭제 - OK (소유자, 멱등 + 이후 조회 404)")
    void delete_test() throws Exception {
        int postId = 3; // ssar(id=2)가 소유한 게시글이라고 가정

        // 1) 최초 삭제
        ResultActions actions = mvc.perform(
                delete("/s/api/posts/{postId}", postId)
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
        ResultActions notFound = mvc.perform(
                        get("/s/api/posts/{postId}", postId)
                                .header("Authorization", "Bearer " + accessToken)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

//        String notFoundBody = notFound.andReturn().getResponse().getContentAsString();
//        System.out.println(notFoundBody);

        // 3) 다시 삭제
        mvc.perform(
                        delete("/s/api/posts/{postId}", postId)
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
    public void getFeedPosts_test() throws Exception {
        // given
        Integer page = 0;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/feed/posts")
                        .param("page", page.toString())
                        .header("Authorization", accessToken)
        );

        // eye
//        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.current").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.size").value(10));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalCount").value(16));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalPage").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.prev").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.next").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFirst").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isLast").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postId").value(23));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].content").value("팔로워 1만 명 감사합니다 \uD83C\uDF89"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].isLiked").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].likesCount").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].commentCount").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].createdAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].user.userId").value(8));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].user.username").value("luna"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].user.profileImageUrl").value(nullValue()));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postImageList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postImageList[0].postImageId").value(45));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postImageList[0].url").isString());
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}