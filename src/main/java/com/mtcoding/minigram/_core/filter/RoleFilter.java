package com.mtcoding.minigram._core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram._core.enums.Role;
import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String uri = request.getRequestURI();

        // (A) 관리자 경로가 아니면 권한 체크 불필요 → 바로 통과
        if (!uri.startsWith("/api/admin")) {
            chain.doFilter(request, response);
            return;
        }

        // (B) 여기부터 관리자 경로: 로그인 + ADMIN 권한 필요
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionUser") == null) {
            exResponse(response, 401, "로그인이 필요합니다."); // 인증 없음 → 401
            return;
        }

        User user = (User) session.getAttribute("sessionUser");
        if (user.getRole() != Role.ADMIN) {
            exResponse(response, 403, "관리자 권한이 필요합니다."); // 인증은 됐지만 권한 없음 → 403
            return;
        }

        chain.doFilter(request, response);
    }

    private void exResponse(HttpServletResponse response, int status, String msg) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(status);
        PrintWriter out = response.getWriter();
        Resp<?> resp = Resp.fail(status, msg);
        out.println(new ObjectMapper().writeValueAsString(resp));
    }
}