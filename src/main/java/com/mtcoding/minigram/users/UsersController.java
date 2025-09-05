package com.mtcoding.minigram.users;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UsersController {
    private final UserService userService;

}
