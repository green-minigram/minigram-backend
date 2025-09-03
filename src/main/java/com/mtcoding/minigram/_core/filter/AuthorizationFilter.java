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
            "/api/auth/check-username"
    );
    private static final Set<String> EXCLUDE_PREFIX = Set.of("/h2-console");

    private boolean isExcluded(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (uri.endsWith("/") && uri.length() > 1) uri = uri.substring(0, uri.length() - 1);
        if (EXCLUDE_EXACT.contains(uri)) return true;
        for (String prefix : EXCLUDE_PREFIX) {
            if (uri.equals(prefix) || uri.startsWith(prefix + "/")) return true;
        }
        return uri.equals("/error") || uri.equals("/favicon.ico");
    }

    // 1) 공개 엔드포인트 정의 (토큰 없어도 통과)
    private boolean isPublic(HttpServletRequest req) {
        String m = req.getMethod();
        String uri = req.getRequestURI();

        // 게시글 읽기(상세/목록/피드 등) 공개
        if ("GET".equals(m) && (uri.matches("^/api/posts/\\d+$") || uri.startsWith("/api/posts"))) return true;

        // 정적/인덱스 등 (필요시 추가)
        if (uri.startsWith("/static/") || "/".equals(uri)) return true;

        return false;
    }

    // 2) 보호 엔드포인트 정의 (토큰 필수)
    private boolean requiresAuth(HttpServletRequest req) {
        String m = req.getMethod();
        String uri = req.getRequestURI();

        // 관리자는 무조건 보호
        if (uri.startsWith("/api/admin")) return true;

        // 쓰기/수정/삭제는 보호
        if (!"GET".equals(m) && uri.startsWith("/api/")) return true;

        return false;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 인증 제외(로그인/회원가입/H2 등)
        if (isExcluded(request)) {
            chain.doFilter(request, response);
            return;
        }

        // 공개 API → 바로 통과
        if (isPublic(request)) {
            chain.doFilter(request, response);
            return;
        }

        // 여기부터 보호 API → 토큰 필수
        String header = request.getHeader("Authorization");
        try {
            if (header == null || header.isBlank()) throw new ExceptionApi400("토큰을 전달해주세요");
            if (!header.startsWith("Bearer ")) throw new ExceptionApi401("유효하지 않은 토큰입니다.");

            String accessToken = header.substring("Bearer ".length());
            User user = JwtUtil.verify(accessToken);

            // 임시: 세션에 사용자 심기 (서비스/컨트롤러에서 사용)
            HttpSession session = request.getSession();
            session.setAttribute("sessionUser", user);

            chain.doFilter(request, response);
        } catch (TokenExpiredException e1) {
            log.error("JWT 만료 예외 발생", e1);
            exResponse(response, "토큰이 만료되었습니다");
        } catch (JWTDecodeException | SignatureVerificationException e2) {
            log.error("JWT 디코딩/서명 검증 실패", e2);
            exResponse(response, "토큰 검증에 실패했어요");
        } catch (RuntimeException e3) {
            log.error("인증 처리 중 예외", e3);
            exResponse(response, e3.getMessage());
        }
    }

    private void exResponse(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(401);
        PrintWriter out = response.getWriter();
        Resp<?> body = Resp.fail(401, msg);
        out.println(new ObjectMapper().writeValueAsString(body));
    }
}
