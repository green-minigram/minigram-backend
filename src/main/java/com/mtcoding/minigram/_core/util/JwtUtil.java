package com.mtcoding.minigram._core.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mtcoding.minigram._core.enums.Role;
import com.mtcoding.minigram.users.User;

import java.util.Date;

public class JwtUtil {
    public static String createRefresh(User user) {
        String jwt = JWT.create()
                .withSubject("blog")
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 일주일
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole().toString())
                .sign(Algorithm.HMAC512("metacoding"));
        return jwt;
    }

    public static String create(User user) {
        // 2025-09-18 00:00:00 기준 만료
        long expireAt = new Date(125, 8, 19, 0, 0, 0).getTime();

        String jwt = JWT.create()
                .withSubject("blog")
                .withExpiresAt(new Date(expireAt)) // System.currentTimeMillis() + 1000 * 60 * 60
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole().toString())
                .sign(Algorithm.HMAC512("metacoding"));
        return jwt;
    }

    public static User verify(String jwt) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("metacoding")).build().verify(jwt);
        int id = decodedJWT.getClaim("id").asInt();
        String username = decodedJWT.getClaim("username").asString();
        String roleStr = decodedJWT.getClaim("role").asString();
        Role role = Role.valueOf(roleStr);

        return User.builder()
                .id(id)
                .username(username)
                .role(role)
                .build();
    }

    // userId만 꺼내는 간단한 메서드 (컨트롤러용)
    public static Integer getUserId(String jwt) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("metacoding")).build().verify(jwt);
        return decodedJWT.getClaim("id").asInt();
    }
}
