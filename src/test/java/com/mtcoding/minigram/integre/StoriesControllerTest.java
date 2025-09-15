package com.mtcoding.minigram.integre;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.stories.StoryRequest;
import com.mtcoding.minigram.users.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class StoriesControllerTest extends MyRestDoc {

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
    public void findByStoryId_test() throws Exception {
        // given
        Integer storyId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/stories/{storyId}", storyId)
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.user.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.user.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.user.profileImageUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.story.storyId").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.story.videoUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.story.thumbnailUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.story.createdAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFollowing").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isOwner").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isLiked").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.likeCount").value(5));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    public void create_test() throws Exception {
        // given
        StoryRequest.CreateDTO reqDTO = new StoryRequest.CreateDTO();
        reqDTO.setVideoUrl("dummy-video.png");


        String requestBody = om.writeValueAsString(reqDTO);
        System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/stories")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyId").isNumber());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.videoUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.thumbnailUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.status").value("ACTIVE"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.createdAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.updatedAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void delete_test() throws Exception {
        // given
        Integer storyId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/stories/{storyId}", storyId)
                        .header("Authorization", accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.storyId").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.videoUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.thumbnailUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.status").value("DELETED"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.createdAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.updatedAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$")));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
