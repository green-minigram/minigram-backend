package com.mtcoding.minigram.users;

import com.mtcoding.minigram._core.enums.Gender;
import com.mtcoding.minigram._core.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@NoArgsConstructor
@Getter
@Table(name = "users_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate birthdate; // "년-월-일"
    private String profileImageUrl;
    private String bio;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public User(Integer id, String email, String username, String password, Role role, String name, Gender gender, LocalDate birthdate, String profileImageUrl, String bio, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.gender = gender;
        this.birthdate = birthdate;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}
