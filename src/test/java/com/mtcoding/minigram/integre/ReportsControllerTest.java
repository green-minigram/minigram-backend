package com.mtcoding.minigram.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.reports.ReportRequest;
import com.mtcoding.minigram.reports.ReportType;
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
public class ReportsControllerTest extends MyRestDoc {
    @Autowired
    private ObjectMapper om;

    private String accessToken1;
    private String accessToken2;

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전에 실행할 코드
        // 일반 유저 ssar
        User ssar = User.builder().id(2).username("ssar").roles("USER").build();
        accessToken1 = JwtUtil.create(ssar);

        // 관리자
        User minigram = User.builder().id(3).username("minigram").roles("ADMIN, USER").build();
        accessToken2 = JwtUtil.create(minigram);
    }

    @Test
    public void create_test() throws Exception {
        // ssar(2)이 6번 게시글(cos(3)의 글) 신고

        // given
        ReportRequest.SaveDTO reqDTO = new ReportRequest.SaveDTO();
        reqDTO.setReportType(ReportType.POST);
        reqDTO.setTargetId(6);
        reqDTO.setReportReasonId(1);


        String requestBody = om.writeValueAsString(reqDTO);
        System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/reports")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", accessToken1)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.reportId").value(15));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.reportType").value("POST"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.targetId").value(6));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.reasonId").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.status").value("PENDING"));

        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
