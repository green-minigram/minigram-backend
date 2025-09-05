package com.mtcoding.minigram.users;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi401;
import com.mtcoding.minigram._core.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @Slf4j
// - Lombok이 자동으로 Logger 필드를 추가해주는 어노테이션
// - log.info()/debug()/error()
@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public UserResponse.JoinDTO join(UserRequest.JoinDTO reqDTO) {
        userRepository.findByEmail(reqDTO.getEmail()).ifPresent(user -> {
            throw new ExceptionApi400("이미 존재하는 email입니다.");
        });

        userRepository.findByUsername(reqDTO.getUsername()).ifPresent(user -> {
            throw new ExceptionApi400("이미 존재하는 username입니다.");
        });

        String encodedPassword = bCryptPasswordEncoder.encode(reqDTO.getPassword());
        User userPS = userRepository.save(reqDTO.toEntity(encodedPassword));

        log.info("[User Join] id={}, username={} 신규 회원 가입 성공", userPS.getId(), userPS.getUsername());

        return new UserResponse.JoinDTO(userPS);
    }

    public UserResponse.LoginDTO login(UserRequest.LoginDTO loginDTO) {
        User userPS = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new ExceptionApi401("유저네임 혹은 비밀번호가 틀렸습니다"));

        Boolean isSame = bCryptPasswordEncoder.matches(loginDTO.getPassword(), userPS.getPassword());
        if (!isSame) throw new ExceptionApi401("유저네임 혹은 비밀번호가 틀렸습니다");

        String accessToken = JwtUtil.create(userPS);

        return new UserResponse.LoginDTO(accessToken, userPS);
    }
}
