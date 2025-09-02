-- 02-follows.sql

INSERT INTO follows_tb (follower_id, followee_id)
VALUES
-- 기존 3인(ssar, cos, love) 서로 맞팔 (총 6건)
(2, 3), -- ssar → cos
(2, 4), -- ssar → love
(3, 2), -- cos → ssar
(3, 4), -- cos → love
(4, 2), -- love → ssar
(4, 3), -- love → cos

-- 인플루언서 패턴: 모두가 luna(8)을 팔로우
(2, 8),
(3, 8),
(4, 8),
(5, 8),
(6, 8),
(7, 8),
(9, 8),

-- 사이클: ssar(2) → mango(5) → hana(6) → ssar(2)
(2, 5),
(5, 6),
(6, 2),

-- 맞팔: mango(5) ↔ neo(7)
(5, 7),
(7, 5),

-- 관찰자 zero(9): 여러 명을 팔로우
(9, 2), -- zero → ssar
(9, 3), -- zero → cos
(9, 4), -- zero → love
(9, 5), -- zero → mango

-- 단방향 엣지들
(4, 6), -- love → hana
(6, 7), -- hana → neo
(3, 7), -- cos → neo
(7, 2), -- neo → ssar

-- luna도 일부 맞팔
(8, 5), -- luna → mango
(8, 3);
-- luna → cos

-- rain(10)은 고립 사용자 → 팔로우 없음
