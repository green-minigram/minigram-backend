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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class FollowsControllerTest extends MyRestDoc {
    @Autowired
    private ObjectMapper om;

    private String accessToken;

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전에 실행할 코드
        User ssar = User.builder().id(2).username("ssar").roles("USER").build();
        accessToken = JwtUtil.create(ssar);
    }

    @Test
    public void create_test() throws Exception {
        // 2번이 6번을 팔로우

        // given
        Integer followeeId = 6;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/users/{followeeId}/follow", followeeId)
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.followId").isNumber());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.followerId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.followeeId").value(6));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void create_fail_test() throws Exception {
        // 2번이 5번을 팔로우 (이미 팔로우 중)

        // given
        Integer followeeId = 5;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/users/{followeeId}/follow", followeeId)
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("이미 팔로우 중입니다"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body").value(Matchers.nullValue()));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void delete_test() throws Exception {
        // 2번이 5번을 팔로우 취소

        // given
        Integer followeeId = 5;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/users/{followeeId}/follow", followeeId)
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.followeeId").value(5));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.message").value("해당 유저에 대한 팔로우를 취소했습니다"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void findFollowers_test() throws Exception {
        // 2번을 팔로우 하는 사람들

        // given
        Integer userId = 2;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/users/{userId}/followers", userId)
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].username").value("neo"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].name").value("네오"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].profileImageUrl").value(Matchers.nullValue()));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].isFollowing").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].isMe").value(false));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void findFollowing_test() throws Exception {
        // 2가 팔로우 하는 사람들

        // given
        Integer userId = 2;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/users/{userId}/following", userId)
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].userId").value(5));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].username").value("mango"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].name").value("망고"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].profileImageUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].isFollowing").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userList[0].isMe").value(false));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
