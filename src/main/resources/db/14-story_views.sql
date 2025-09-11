-- 09-story-views.sql

INSERT INTO story_views_tb (story_id, user_id, created_at)
VALUES
-- ssar(2)의 첫 번째 스토리를 본 사람들
(1, 3, now()),  -- cos
(1, 5, now()),  -- mango
(1, 8, now()),  -- luna

-- cos(3)의 첫 번째 스토리를 본 사람들
(2, 2, now()),  -- ssar
(2, 4, now()),  -- love
(2, 7, now()),  -- neo

-- love(4)의 첫 번째 스토리(HIDDEN은 무시)
-- 대신 love의 ACTIVE 스토리(id=11) 기준
(11, 2, now()), -- ssar
(11, 5, now()), -- mango
(11, 9, now()), -- zero

-- mango(5)의 첫 번째 스토리
(4, 2, now()),  -- ssar
(4, 3, now()),  -- cos
(4, 6, now()),  -- hana

-- hana(6)의 ACTIVE 스토리(id=21)
(21, 2, now()), -- ssar
(21, 5, now()), -- mango
(21, 9, now()), -- zero

-- neo(7)의 ACTIVE 스토리(id=6)
(6, 2, now()),  -- ssar
(6, 3, now()),  -- cos
(6, 8, now()),  -- luna

-- luna(8)의 첫 번째 스토리(id=7)
(7, 2, now()),  -- ssar
(7, 3, now()),  -- cos
(7, 9, now()),  -- zero
(7, 10, now()), -- rain

-- zero(9)의 첫 번째 스토리(id=8)
(8, 2, now()),  -- ssar
(8, 4, now()),  -- love
(8, 7, now()),  -- neo

-- rain(10)의 첫 번째 스토리(id=9)
(9, 2, now()),  -- ssar
(9, 3, now()),  -- cos
(9, 5, now()),  -- mango
(9, 8, now()); -- luna
