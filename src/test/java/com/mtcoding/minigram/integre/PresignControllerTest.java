package com.mtcoding.minigram.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.enums.Role;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.storage.PresignRequest;
import com.mtcoding.minigram.storage.UploadType;
import com.mtcoding.minigram.users.User;
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
public class PresignControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    private String accessToken;

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전에 실행할 코드
        User ssar = User.builder().id(2).username("ssar").role(Role.USER).build();
        accessToken = JwtUtil.create(ssar);
    }

    @Test
    public void update_test() throws Exception {
        // given

        PresignRequest.UploadDTO reqDTO = new PresignRequest.UploadDTO();
        reqDTO.setUploadType(UploadType.IMAGE);
        reqDTO.setMimeType("image/png");


        String requestBody = om.writeValueAsString(reqDTO);
        System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/api/storage/presignUrl")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.key").value(""));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.presignedUrl").value(""));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.expiresIn").value(900));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.mimeType").value("image/png"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

}