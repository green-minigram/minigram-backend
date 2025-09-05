-- 06-post_likes.sql

INSERT INTO post_likes_tb (post_id, user_id, created_at)
VALUES
-- 광고글 (관리자 글)
(1, 2, now()), -- ssar → 광고글 좋아요
(1, 3, now()), -- cos
(1, 5, now()), -- mango
(2, 4, now()), -- love
(2, 6, now()), -- hana
(2, 7, now()), -- neo

-- ssar(2)의 글 (post 3,4)
(3, 3, now()), -- cos
(3, 4, now()), -- love
(3, 5, now()), -- mango
(4, 6, now()), -- hana
(4, 7, now()), -- neo

-- cos(3)의 글 (post 5,6)
(5, 2, now()), -- ssar
(5, 4, now()), -- love
(5, 7, now()), -- neo
(6, 2, now()), -- ssar
(6, 5, now()), -- mango

-- love(4)의 글 (post 7)
(7, 2, now()), -- ssar
(7, 3, now()), -- cos
(7, 5, now()), -- mango

-- mango(5)의 글 (post 8)
(8, 2, now()), -- ssar
(8, 3, now()), -- cos
(8, 4, now()), -- love

-- hana(6)의 글 (post 9)
(9, 2, now()),
(9, 3, now()),
(9, 7, now()),

-- neo(7)의 글 (post 10)
(10, 2, now()),
(10, 3, now()),
(10, 4, now()),

-- luna(8)의 인기글 모음 (post 11~23, 특히 18번에 집중)
(11, 2, now()),
(12, 3, now()),
(13, 4, now()),
(14, 5, now()),
(15, 6, now()),
(16, 7, now()),
(17, 9, now()),
-- post 18 → 인기글 (많이 몰림)
(18, 2, now()),
(18, 3, now()),
(18, 4, now()),
(18, 5, now()),
(18, 6, now()),
(18, 7, now()),
(18, 9, now()),
(18, 10, now()),
-- 나머지 luna 글에도 약간씩
(19, 2, now()),
(20, 3, now()),
(21, 4, now()),
(22, 5, now()),
(23, 6, now()),

-- zero(9)의 글 (post 24)
(24, 2, now()),
(24, 3, now()),

-- rain(10)의 글 (post 25)
(25, 2, now()),
(25, 5, now());
