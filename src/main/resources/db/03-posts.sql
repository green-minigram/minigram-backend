-- 03-posts.sql

INSERT INTO posts_tb (user_id, content, status, created_at, updated_at)
VALUES
-- 관리자 광고 글 (user_id = 1)
(1, '📢 [광고] 지금 가입하면 특별 혜택을 드립니다!', 'ACTIVE', now(), now()),
(1, '📢 [광고] 미니그램 신규 기능을 소개합니다 🚀', 'ACTIVE', now(), now()),

-- ssar(2)
(2, '오늘은 DB 설계 공부 중 📚', 'ACTIVE', now(), now()),
(2, 'Spring Boot + JPA 연동 성공! 🚀', 'ACTIVE', now(), now()),

-- cos(3)
(3, '제주도 여행 사진 모음 🏝️', 'ACTIVE', now(), now()),
(3, '사진 촬영 팁 공유합니다 📸', 'ACTIVE', now(), now()),

-- love(4)
(4, 'Love wins all ❤️', 'HIDDEN', now(), now()),

-- mango(5)
(5, '망고주스가 최고 🥭', 'ACTIVE', now(), now()),

-- hana(6)
(6, '카페에서 공부하는 중 ☕', 'ACTIVE', now(), now()),

-- neo(7)
(7, '알고리즘 문제 3시간 만에 풀었다! 💻', 'ACTIVE', now(), now()),

-- luna(8) → 다작 유저 (총 13건)
(8, '오늘 업로드한 영상 봐주세요 🎥', 'ACTIVE', now(), now()),
(8, '촬영 비하인드 컷 공개 🎬', 'ACTIVE', now(), now()),
(8, '새로운 프로젝트 준비 중… 기대해 주세요 ✨', 'ACTIVE', now(), now()),
(8, '팬들과 함께하는 라이브 Q&A 🎤', 'ACTIVE', now(), now()),
(8, '편집하다가 밤샜다 😵', 'ACTIVE', now(), now()),
(8, '내가 쓰는 장비 소개 🎥', 'ACTIVE', now(), now()),
(8, '오늘도 구독해주셔서 감사합니다 🙏', 'ACTIVE', now(), now()),
(8, '브이로그: 하루 일상 ☀️', 'ACTIVE', now(), now()),
(8, '여행 브이로그 준비 중 ✈️', 'ACTIVE', now(), now()),
(8, '댓글 이벤트 진행 중 🎁', 'ACTIVE', now(), now()),
(8, '오늘의 추천 음악 🎶', 'ACTIVE', now(), now()),
(8, '촬영 장소 비하인드 공개 🏞️', 'ACTIVE', now(), now()),
(8, '팔로워 1만 명 감사합니다 🎉', 'ACTIVE', now(), now()),

-- zero(9)
(9, '나는 팔로우만 하고 글은 잘 안 씀 ㅋㅋ', 'HIDDEN', now(), now()),

-- rain(10)
(10, '첫 글이에요. 아직은 조용히 시작합니다 ☔', 'ACTIVE', now(), now());
