-- 09-story-likes.sql

INSERT INTO story_likes_tb (story_id, user_id, created_at)
VALUES
-- ssar(2)가 다른 유저들의 스토리에 좋아요
(2, 2, now()),  -- cos의 스토리 좋아요
(3, 2, now()),  -- love의 스토리 좋아요
(4, 2, now()),  -- mango의 스토리 좋아요

-- cos(3)
(1, 3, now()),  -- ssar의 스토리 좋아요
(5, 3, now()),  -- hana의 스토리 좋아요
(6, 3, now()),  -- neo의 스토리 좋아요

-- love(4)
(1, 4, now()),  -- ssar의 스토리 좋아요
(2, 4, now()),  -- cos의 스토리 좋아요
(7, 4, now()),  -- luna의 스토리 좋아요

-- mango(5)
(1, 5, now()),  -- ssar의 스토리 좋아요
(8, 5, now()),  -- zero의 스토리 좋아요
(9, 5, now()),  -- rain의 스토리 좋아요

-- hana(6)
(2, 6, now()),  -- cos의 스토리 좋아요
(3, 6, now()),  -- love의 스토리 좋아요
(7, 6, now()),  -- luna의 스토리 좋아요

-- neo(7)
(1, 7, now()),  -- ssar의 스토리 좋아요
(4, 7, now()),  -- mango의 스토리 좋아요
(8, 7, now()),  -- zero의 스토리 좋아요

-- luna(8)
(1, 8, now()),  -- ssar의 스토리 좋아요
(5, 8, now()),  -- hana의 스토리 좋아요
(9, 8, now()),  -- rain의 스토리 좋아요

-- zero(9)
(2, 9, now()),  -- cos의 스토리 좋아요
(6, 9, now()),  -- neo의 스토리 좋아요
(7, 9, now()),  -- luna의 스토리 좋아요

-- rain(10)
(3, 10, now()), -- love의 스토리 좋아요
(4, 10, now()), -- mango의 스토리 좋아요
(8, 10, now()); -- zero의 스토리 좋아요
