package com.mtcoding.minigram.users;

import com.mtcoding.minigram._core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;

    @PostMapping("/api/auth/join")
    public ResponseEntity<?> join(@RequestBody UserRequest.JoinDTO reqDTO) {
        var respDTO = userService.join(reqDTO);
        return Resp.ok(respDTO);
    }

//    @PostMapping("/api/auth/login")
//    public ResponseEntity<?> login(@RequestBody UserRequest.LoginDTO reqDTO) {
//        String accessToken = userService.login(reqDTO);
//        return Resp.ok(accessToken);
//    }
}
