package com.mtcoding.minigram.advertisements;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi403;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.posts.PostStatus;
import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.posts.images.PostImageRepository;
import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// @Slf4j
// - Lombok이 자동으로 Logger 필드를 추가해주는 어노테이션
// - log.info()/debug()/error()
@Slf4j
@RequiredArgsConstructor
@Service
public class AdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UserRepository userRepository;

    @Transactional
    public AdvertisementResponse.DetailDTO create(AdvertisementRequest.CreateDTO req, Integer adminUserId) {

        if (req.getStartAt().isAfter(req.getEndAt())) {
            throw new ExceptionApi400("startAt은 endAt보다 이후일 수 없습니다.");
        }

        // 1) 관리자 로딩 + 권한 체크
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ExceptionApi404("사용자를 찾을 수 없습니다."));
        if (!admin.getRoles().contains("ADMIN")) {
            throw new ExceptionApi403("관리자 권한이 필요합니다.");
        }

        // 2) 이미지 URL 정제(포스트 서비스와 동일한 패턴)
        List<String> cleanedUrls = Optional.ofNullable(req.getImageUrls())
                .orElse(List.of())
                .stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        if (cleanedUrls.isEmpty()) throw new ExceptionApi400("이미지 최소 1장은 필요합니다.");
        if (cleanedUrls.size() > 10) throw new ExceptionApi400("이미지는 최대 10장까지 가능합니다.");

        // 3) Post 저장 (상태 NOT NULL 주의)
        Post post = Post.builder()
                .user(admin)
                .content(req.getContent())
                .status(PostStatus.ACTIVE)
                .build();
        postRepository.save(post);

        // 4) PostImage 저장 (정렬 필요 없으면 생략 / 필요하면 sort 컬럼 사용)
        List<PostImage> savedImages = new ArrayList<>(cleanedUrls.size());
        for (String url : cleanedUrls) {
            PostImage pi = PostImage.builder()
                    .post(post)
                    .url(url)
                    .build();
            postImageRepository.save(pi);
            savedImages.add(pi);
        }

        // 5) Advertisement 저장 (@MapsId → PK = post_id)
        Advertisement ad = Advertisement.builder()
                .post(post)
                .user(admin)
                .status(AdvertisementStatus.ACTIVE)
                .startAt(req.getStartAt())
                .endAt(req.getEndAt())
                .build();
        advertisementRepository.save(ad);

        // 6) 응답 (공유 PK라 adId == postId)
        return AdvertisementResponse.DetailDTO.from(ad);
    }
}
