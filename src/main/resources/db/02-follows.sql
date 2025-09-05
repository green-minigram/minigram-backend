-- 02-follows.sql

INSERT INTO follows_tb (follower_id, followee_id, created_at)
VALUES
-- 기존 3인(ssar, cos, love) 서로 맞팔 (총 6건)
(2, 3, now()), -- ssar → cos
(2, 4, now()), -- ssar → love
(3, 2, now()), -- cos → ssar
(3, 4, now()), -- cos → love
(4, 2, now()), -- love → ssar
(4, 3, now()), -- love → cos

-- 인플루언서 패턴: 모두가 luna(8)을 팔로우
(2, 8, now()),
(3, 8, now()),
(4, 8, now()),
(5, 8, now()),
(6, 8, now()),
(7, 8, now()),
(9, 8, now()),

-- 사이클: ssar(2) → mango(5) → hana(6) → ssar(2)
(2, 5, now()),
(5, 6, now()),
(6, 2, now()),

-- 맞팔: mango(5) ↔ neo(7)
(5, 7, now()),
(7, 5, now()),

-- 관찰자 zero(9): 여러 명을 팔로우
(9, 2, now()), -- zero → ssar
(9, 3, now()), -- zero → cos
(9, 4, now()), -- zero → love
(9, 5, now()), -- zero → mango

-- 단방향 엣지들
(4, 6, now()), -- love → hana
(6, 7, now()), -- hana → neo
(3, 7, now()), -- cos → neo
(7, 2, now()), -- neo → ssar

-- luna도 일부 맞팔
(8, 5, now()), -- luna → mango
(8, 3, now());
-- luna → cos

-- rain(10)은 고립 사용자 → 팔로우 없음
