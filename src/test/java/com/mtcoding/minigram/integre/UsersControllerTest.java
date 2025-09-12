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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UsersControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    private String accessToken1;
    private String accessToken2;

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전에 실행할 코드
        User minigram = User.builder().id(1).username("minigram").roles("ADMIN, USER").build();
        accessToken1 = JwtUtil.create(minigram);
        User ssar = User.builder().id(2).username("ssar").roles("USER").build();
        accessToken2 = JwtUtil.create(ssar);
    }

    @Test
    public void getUserProfile_test() throws Exception {
        // given
        Integer userId = 3;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/users/{userId}/profile", userId)
                        .header("Authorization", accessToken2)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(3));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.username").value("cos"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.bio").isEmpty());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.profileImageUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.hasUnseen").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isOwner").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFollowing").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postCount").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.followerCount").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.followingCount").value(4));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void getMyProfile_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/users/me/profile")
                        .header("Authorization", accessToken2)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.bio").value("백엔드 개발자 지망생"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.profileImageUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.hasUnseen").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isOwner").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFollowing").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postCount").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.followerCount").value(5));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.followingCount").value(4));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void getUserPosts_test() throws Exception {
        // given
        Integer userId = 2;
        Integer page = 0;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/users/{userId}/posts", userId)
                        .param("page", page.toString())
                        .header("Authorization", accessToken2)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.current").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.size").value(12));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalCount").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalPage").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.prev").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.next").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFirst").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isLast").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postId").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postImageUrl").isString());
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void getMyPosts_test() throws Exception {
        // given
        Integer page = 0;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/users/me/posts")
                        .param("page", page.toString())
                        .header("Authorization", accessToken2)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.current").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.size").value(12));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalCount").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalPage").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.prev").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.next").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFirst").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isLast").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postId").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postList[0].postImageUrl").isString());
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void getUserStories_test() throws Exception {
        // 2번 유저가 3번 유저의 게시글 조회
        // given
        Integer userId = 3;
        Integer page = 0;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/users/{userId}/stories", userId)
                        .param("page", page.toString())
                        .header("Authorization", accessToken2)
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
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].storyId").value(18));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].thumbnailUrl").isString());
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void getMyStories_test() throws Exception {
        // given
        Integer page = 0;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/users/me/stories")
                        .param("page", page.toString())
                        .header("Authorization", accessToken2)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.current").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.size").value(12));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalCount").value(6));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalPage").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.prev").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.next").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFirst").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isLast").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].storyId").value(14));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyList[0].thumbnailUrl").isString());
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}