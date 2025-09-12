package com.mtcoding.minigram.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.users.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class FeedControllerTest extends MyRestDoc {

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
    public void findPosts_test() throws Exception {
        // given
        Integer page = 2;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/feed/posts")
                        .param("page", page.toString())
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.current").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.size").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalCount").value(18));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalPage").value(3));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.prev").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.next").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFirst").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isLast").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].isAdvertisement").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postId").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].content").value("Spring Boot + JPA 연동 성공! 🚀"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].isOwner").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].isLiked").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].likesCount").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].commentCount").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].createdAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].user.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].user.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].user.profileImageUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].user.isFollowing").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postImageList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postImageList[0].postImageId").value(6));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postImageList[0].url").isString());
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void findStoryPreviews_test() throws Exception {
        // given
        Integer page = 0;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/feed/story-previews")
                        .param("page", page.toString())
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.current").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.size").value(10));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalCount").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalPage").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.prev").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.next").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFirst").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isLast").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyHeadList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyHeadList[0].userId").value(8));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyHeadList[0].username").value("luna"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyHeadList[0].hasUnseen").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyHeadList[0].profileImageUrl").value(Matchers.nullValue()));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void findMyStories_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/feed/users/me/stories")
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].user.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].user.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].user.profileImageUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].story.storyId").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].story.videoUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].story.thumbnailUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].story.createdAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].isFollowing").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].isOwner").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].isLiked").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].likeCount").value(5));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void findStoriesByUserId_test() throws Exception {
        // given
        Integer userId = 2;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/feed/users/{userId}/stories", userId)
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].user.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].user.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].user.profileImageUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].story.storyId").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].story.videoUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].story.thumbnailUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].story.createdAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].isFollowing").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].isOwner").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].isLiked").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].likeCount").value(5));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}