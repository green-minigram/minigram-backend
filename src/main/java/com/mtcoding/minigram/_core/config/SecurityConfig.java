package com.mtcoding.minigram._core.config;

import com.mtcoding.minigram._core.error.Jwt401Handler;
import com.mtcoding.minigram._core.error.Jwt403Handler;
import com.mtcoding.minigram._core.filter.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 컨텍스트 홀더에 세션 저장할 때 사용하는 클래스
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. iframe 허용 -> mysql로 전환하면 삭제
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // 2. csrf 비활성화 -> html 사용 안할꺼니까!!
        http.csrf(csrf -> csrf.disable());

        // 3. 세션 비활성화 (STATELESS) -> 키 전달 안해주고, 집에갈때 락카를 비워버린다.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 4. 폼 로그인 비활성화 (UsernamePasswordAuthenticationFilter 발동을 막기)
        http.formLogin(form -> form.disable());

        // 5. HTTP Basic 인증 비활성화 (BasicAuthenticationFilter 발동을 막기)
        http.httpBasic(basicLogin -> basicLogin.disable());

        // 6. 커스텀 필터 작창 (인가 필터) -> 로그인 컨트롤러에서 직접하기
        http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 7. 예외처리 핸들러 등록 ((1)인증,인가가 완료되면 어떻게? (2)예외가 발생하면 어떻게?)
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(new Jwt401Handler())
                .accessDeniedHandler(new Jwt403Handler()));

        // /s/api/** : 인증 필요, /s/api/admin/** : ADMIN 권한 필요
        http.authorizeHttpRequests(
                authorize -> authorize
                        // 공개 엔드포인트
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/docs/**").permitAll()
                        // 권한 필요
                        .requestMatchers("/s/api/admin/**").hasRole("ADMIN")
                        // 인증 필요
                        .requestMatchers("/s/api/**").authenticated()
                        .anyRequest().permitAll()
        );

        return http.build();
    }


}