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
public class NotificationControllerTest extends MyRestDoc {

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
    public void findAllWithinOneMonth_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/notifications")
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].notificationId").value(10));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].type").value("FOLLOWED"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].sender.userId").value(3));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].sender.username").value("cos"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].sender.profileImageUrl").value("https://picsum.photos/seed/cos/200"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].sender.isFollowing").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].targetId").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].postId").value(Matchers.nullValue()));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].postImageUrl").value(Matchers.nullValue()));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].commentContent").value(Matchers.nullValue()));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.notificationList[0].readStatus").value("READ"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
