package com.mtcoding.minigram.advertisements;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

@Import(AdvertisementRepository.class)
@DataJpaTest
public class AdvertisementRepositoryTest {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Test
    public void findAllValid_test() {
        int currentUserId = 2;
        int page = 1;
        LocalDateTime now = LocalDateTime.now();
        int totalCount = 3;

        List<Object[]> obsList = advertisementRepository.findAllValid(page, currentUserId, now, totalCount);
        System.out.println("=========== 게시 기간 내 활성화된 광고 조회 ============");
        for (Object[] obs : obsList) {
            Advertisement ad = (Advertisement) obs[0];
            int likesCount = ((Number) obs[1]).intValue();
            boolean isLiked = (Boolean) obs[2];
            int commentCount = ((Number) obs[3]).intValue();

            var post = ad.getPost();
            var owner = ad.getUser();

            System.out.println("postId         : " + (post != null ? post.getId() : null));
            System.out.println("ownerId        : " + (owner != null ? owner.getId() : null));
            System.out.println("ownerUsername  : " + (owner != null ? owner.getUsername() : null));
            System.out.println("period         : " + ad.getStartAt() + " ~ " + ad.getEndAt());
            System.out.println("likesCount     : " + likesCount);
            System.out.println("isLiked(by " + currentUserId + "): " + isLiked);
            System.out.println("commentCount   : " + commentCount);
            System.out.println("------------------------------------------");
        }
        System.out.println("==========================================");
    }
}
