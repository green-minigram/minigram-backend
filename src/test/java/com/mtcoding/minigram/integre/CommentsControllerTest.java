package com.mtcoding.minigram.integre;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.posts.comments.CommentRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CommentsControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    private String accessToken;

    @BeforeEach
    void setUp() {
        User ssar = User.builder().id(2).username("ssar").roles("USER").build();
        accessToken = JwtUtil.create(ssar);
    }

    @Test
    public void find_all_by_post_id_test() throws Exception {
        // given
        Integer postId = 18;
        Integer page = 0;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/posts/{postId}/comments", postId)
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
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalCount").value(15));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalPage").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.prev").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.next").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFirst").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isLast").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].commentId").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].rootId").value(Matchers.nullValue()));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].parentId").value(Matchers.nullValue()));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].content").value("첫 댓글! 영상 너무 재밌어요 \uD83D\uDE4C"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].isOwner").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].isLiked").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].likeCount").value(3));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].user.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].user.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].user.profileImageUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].user.hasUnseen").value(true));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    void delete_test() throws Exception {
        int commentId = 1; // ssar(2번)의 댓글

        ResultActions actions = mvc.perform(delete("/s/api/comments/{commentId}", commentId)
                .header("Authorization", accessToken));

        String responseBody = actions.andReturn().getResponse().getContentAsString();
        // System.out.println(responseBody);

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.body.commentId").value(commentId))
                .andExpect(jsonPath("$.body.message").value("댓글을 삭제했습니다."))
                .andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void find_replies_by_root_test() throws Exception {
        // given
        Integer commentId = 2;
        Integer page = 0;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/comments/{commentId}/replies", commentId)
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
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalCount").value(3));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.totalPage").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.prev").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.next").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isFirst").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.isLast").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList").isArray());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].commentId").value(8));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].rootId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].parentId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].content").value("222 비하인드 좋아요"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].isOwner").value(true));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].isLiked").value(false));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].likeCount").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].user.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].user.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].user.profileImageUrl").isString());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentList[0].user.hasUnseen").value(true));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void create_test() throws Exception {
        // given
        Integer postId = 18;

        CommentRequest.CreateDTO reqDTO = new CommentRequest.CreateDTO();
        reqDTO.setContent("좋은 아이디어네요! 다음 영상도 기대할게요 😊");
        reqDTO.setParentId(2);

        String requestBody = om.writeValueAsString(reqDTO);
        // System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/posts/{postId}/comments", postId)
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
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentId").value(43));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postId").value(18));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.rootId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.parentId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.content").value("좋은 아이디어네요! 다음 영상도 기대할게요 \uD83D\uDE0A"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.status").value("ACTIVE"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.updateAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?")));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void update_test() throws Exception {
        // given
        Integer commentId = 41;

        CommentRequest.UpdateDTO reqDTO = new CommentRequest.UpdateDTO();
        reqDTO.setContent("댓글 수정했어요");

        String requestBody = om.writeValueAsString(reqDTO);
        // System.out.println(requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/comments/{commentId}", commentId)
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
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.commentId").value(41));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.postId").value(24));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.userId").value(2));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.rootId").value(Matchers.nullValue()));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.parentId").value(Matchers.nullValue()));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.content").value("댓글 수정했어요"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.status").value("ACTIVE"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.updateAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?")));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}
