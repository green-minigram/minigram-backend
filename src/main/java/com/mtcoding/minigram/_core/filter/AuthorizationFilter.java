package com.mtcoding.minigram._core.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi401;
import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@Slf4j
public class AuthorizationFilter implements Filter {

    private static final Set<String> EXCLUDE_EXACT = Set.of(
            "/api/auth/join",
            "/api/auth/login",
            "/api/auth/check-email",
            "/api/auth/check-username",
            "/users/1/token",
            "/users/2/token"
    );

    // 하위 경로 전체 허용
    private static final Set<String> EXCLUDE_PREFIX = Set.of(
            "/h2-console"     // /h2-console, /h2-console/** 모두 허용
    );

    private boolean isExcluded(HttpServletRequest req) {
        String uri = req.getRequestURI();

        if (uri.endsWith("/") && uri.length() > 1) {
            uri = uri.substring(0, uri.length() - 1);
        }

        if (EXCLUDE_EXACT.contains(uri)) return true;

        for (String prefix : EXCLUDE_PREFIX) {
            if (uri.equals(prefix) || uri.startsWith(prefix + "/")) return true;
        }

        if (uri.equals("/error") || uri.equals("/favicon.ico")) return true;

        return false;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (isExcluded(request)) {
            chain.doFilter(request, response);
            return;
        }

        String accessToken = request.getHeader("Authorization");

        try {
            if (accessToken == null || accessToken.isBlank()) throw new ExceptionApi400("토큰을 전달해주세요");
            if (!accessToken.startsWith("Bearer ")) throw new ExceptionApi401("유효하지 않은 토큰입니다.");

            accessToken = accessToken.replace("Bearer ", "");


            User user = JwtUtil.verify(accessToken);

            // 토큰을 다시 검증하기 귀찮아서, 임시로 세션에 넣어둔거다.
            HttpSession session = request.getSession();
            session.setAttribute("sessionUser", user);

            chain.doFilter(request, response);
        } catch (TokenExpiredException e1) {
            log.error("JWT 만료 예외 발생", e1);
            exResponse(response, "토큰이 만료되었습니다");
        } catch (JWTDecodeException | SignatureVerificationException e2) {
            log.error("JWT 디코딩 또는 서명 검증 실패", e2);
            exResponse(response, "토큰 검증에 실패했어요");
        } catch (RuntimeException e3) {
            log.error("기타 런타임 예외 발생", e3);
            exResponse(response, e3.getMessage());
        }
    }

    private void exResponse(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(401);
        PrintWriter out = response.getWriter();

        Resp<?> resp = Resp.fail(401, msg);
        String responseBody = new ObjectMapper().writeValueAsString(resp);
        out.println(responseBody);
    }
}
