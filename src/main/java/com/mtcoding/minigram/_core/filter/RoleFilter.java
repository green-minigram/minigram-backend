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
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("sessionUser") == null) {
            exResponse(response, "로그인 필요합니다.");
            return;
        }

        User user = (User) session.getAttribute("sessionUser");
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/admin") && user.getRole() != Role.ADMIN) {
            exResponse(response, "관리자 권한이 필요합니다.");
            return;
        }

        chain.doFilter(request, response);
    }

    private void exResponse(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(403);
        PrintWriter out = response.getWriter();

        Resp<?> resp = Resp.fail(403, msg);
        String responseBody = new ObjectMapper().writeValueAsString(resp);
        out.println(responseBody);
    }
}
