package com.mtcoding.minigram.users;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@Import(UserRepository.class)
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByEmail_test() {
        String email = "ssar@nate.com";
        Optional<User> userOP = userRepository.findByEmail(email);
        System.out.println("===========이메일중복체크============");
        System.out.println("email: " + email + ", " + userOP.get().getEmail());
        System.out.println("===========이메일중복체크============");
    }

    @Test
    public void findByUsername_test() {
        String username = "ssar";
        Optional<User> userOP = userRepository.findByUsername(username);
        System.out.println("===========유저네임중복체크============");
        System.out.println("username: " + username + ", " + userOP.get().getUsername());
        System.out.println("===========유저네임중복체크============");
    }
}
