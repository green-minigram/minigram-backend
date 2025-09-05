package com.mtcoding.minigram.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram.MyRestDoc;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
        User ssar = User.builder().id(1).username("minigram").roles("ADMIN, USER").build();
        accessToken1 = JwtUtil.create(ssar);
        User cos = User.builder().id(2).username("ssar").roles("USER").build();
        accessToken2 = JwtUtil.create(cos);
    }

}