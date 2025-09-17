package com.mtcoding.minigram.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.reports.ReportRequest;
import com.mtcoding.minigram.reports.ReportType;
import com.mtcoding.minigram.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        User minigram = User.builder().id(1).username("minigram").roles("ADMIN, USER").build();
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
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.reportId").value(15));
        actions.andExpect(jsonPath("$.body.reportType").value("POST"));
        actions.andExpect(jsonPath("$.body.targetId").value(6));
        actions.andExpect(jsonPath("$.body.userId").value(2));
        actions.andExpect(jsonPath("$.body.reasonId").value(1));
        actions.andExpect(jsonPath("$.body.status").value("PENDING"));

        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_reasons_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                get("/s/api/reports/reasons")
                        .header("Authorization", accessToken1)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.body.reasonList").isArray());
        actions.andExpect(jsonPath("$.body.reasonList[0].id").value(1));
        actions.andExpect(jsonPath("$.body.reasonList[0].code").value("DISLIKE"));
        actions.andExpect(jsonPath("$.body.reasonList[0].label").value("마음에 들지 않습니다"));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("신고 상세 - POST - OK")
    void find_post_test() throws Exception {
        // 1) 호출
        int reportId = 1; // POST 타입
        ResultActions actions = mvc.perform(
                get("/s/api/admin/reports/{reportId}", reportId)
                        .header("Authorization", accessToken2)
                        .accept(MediaType.APPLICATION_JSON)
        );

        String responseBody = actions.andReturn().getResponse().getContentAsString();
//        System.out.println(responseBody);

        // 2) 검증
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.reportId").value(1))
                .andExpect(jsonPath("$.body.reportedAt",
                        matchesPattern("\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}.*")))

                .andExpect(jsonPath("$.body.reporter.userId").value(2))
                .andExpect(jsonPath("$.body.reporter.username").value("ssar"))
                .andExpect(jsonPath("$.body.reporter.profileImageUrl").value("https://picsum.photos/seed/ssar/200"))

                .andExpect(jsonPath("$.body.reportedObject.type").value("POST"))
                .andExpect(jsonPath("$.body.reportedObject.objectId").value(18))
                .andExpect(jsonPath("$.body.reportedObject.author.userId").value(8))
                .andExpect(jsonPath("$.body.reportedObject.author.username").value("luna"))
                .andExpect(jsonPath("$.body.reportedObject.author.profileImageUrl").doesNotExist())

                .andExpect(jsonPath("$.body.reportedObject.mediaList", hasSize(10)))
                .andExpect(jsonPath("$.body.reportedObject.mediaList[0].type").value("IMAGE"))
                .andExpect(jsonPath("$.body.reportedObject.mediaList[0].url").value("https://picsum.photos/seed/luna8_a/800/600"))

                .andExpect(jsonPath("$.body.reportedObject.content").value("브이로그: 하루 일상 ☀️"))
                .andExpect(jsonPath("$.body.reportedObject.postedAt",
                        matchesPattern("\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}.*")))

                .andExpect(jsonPath("$.body.reportedObject.likes.count").value(8))
                .andExpect(jsonPath("$.body.reportedObject.likes.isLiked").value(false))
                .andExpect(jsonPath("$.body.reportedObject.commentsCount").value(15))

                .andExpect(jsonPath("$.body.reportReasonLabel").value("스팸, 사기 또는 스팸"))
                .andExpect(jsonPath("$.body.status").value("PENDING"));

        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("신고 상세 - STORY - OK(POST 전용 필드 미출력)")
    void find_story_test() throws Exception {
        // 1) 호출
        int reportId = 8; // STORY 타입
        ResultActions actions = mvc.perform(
                get("/s/api/admin/reports/{reportId}", reportId)
                        .header("Authorization", "Bearer " + accessToken2)
                        .accept(MediaType.APPLICATION_JSON)
        );

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // 2) 검증
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.reportId").value(8))
                .andExpect(jsonPath("$.body.reportedAt",
                        matchesPattern("\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}.*")))

                .andExpect(jsonPath("$.body.reporter.userId").value(2))
                .andExpect(jsonPath("$.body.reporter.username").value("ssar"))
                .andExpect(jsonPath("$.body.reporter.profileImageUrl").value("https://picsum.photos/seed/ssar/200"))

                .andExpect(jsonPath("$.body.reportedObject.type").value("STORY"))
                .andExpect(jsonPath("$.body.reportedObject.objectId").value(3))
                .andExpect(jsonPath("$.body.reportedObject.author.userId").value(4))
                .andExpect(jsonPath("$.body.reportedObject.author.username").value("love"))
                .andExpect(jsonPath("$.body.reportedObject.author.profileImageUrl").doesNotExist())

                .andExpect(jsonPath("$.body.reportedObject.mediaList", hasSize(2)))
                .andExpect(jsonPath("$.body.reportedObject.mediaList[0].type").value("VIDEO"))
                .andExpect(jsonPath("$.body.reportedObject.mediaList[0].url").value("https://cdn.pixabay.com/video/2020/01/22/31495-387312407_tiny.mp4"))
                .andExpect(jsonPath("$.body.reportedObject.mediaList[1].type").value("IMAGE"))
                .andExpect(jsonPath("$.body.reportedObject.mediaList[1].url").value("https://picsum.photos/seed/story03/400/300"))

                .andExpect(jsonPath("$.body.reportedObject.postedAt",
                        matchesPattern("\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}.*")))
                .andExpect(jsonPath("$.body.reportedObject.likes.count").value(3))
                .andExpect(jsonPath("$.body.reportedObject.likes.isLiked").value(false))

                .andExpect(jsonPath("$.body.reportReasonLabel").value("나체 이미지 또는 성적 행위"))
                .andExpect(jsonPath("$.body.status").value("APPROVED"));

        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
