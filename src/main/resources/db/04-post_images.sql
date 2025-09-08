-- 04-post_images.sql
-- 규칙: 각 post 최소 1장, 최대 10장
-- URL 예시는 picsum.photos (seed로 고정 재현성 부여)

INSERT INTO post_images_tb (post_id, url, sort)
VALUES
-- 광고글 (post 1~2)
(1, 'https://picsum.photos/seed/ad1_a/800/600', 0),
(1, 'https://picsum.photos/seed/ad1_b/800/600', 1),
(2, 'https://picsum.photos/seed/ad2_a/800/600', 0),

-- ssar (post 3~4)
(3, 'https://picsum.photos/seed/ssar1_a/800/600', 0),
(3, 'https://picsum.photos/seed/ssar1_b/800/600', 1),
(4, 'https://picsum.photos/seed/ssar2_a/800/600', 0),

-- cos (post 5~6)
(5, 'https://picsum.photos/seed/cos1_a/800/600', 0),
(6, 'https://picsum.photos/seed/cos2_a/800/600', 0),
(6, 'https://picsum.photos/seed/cos2_b/800/600', 1),

-- love (post 7)
(7, 'https://picsum.photos/seed/love1_a/800/600', 0),

-- mango (post 8)
(8, 'https://picsum.photos/seed/mango1_a/800/600', 0),
(8, 'https://picsum.photos/seed/mango1_b/800/600', 1),

-- hana (post 9)
(9, 'https://picsum.photos/seed/hana1_a/800/600', 0),

-- neo (post 10)
(10, 'https://picsum.photos/seed/neo1_a/800/600', 0),
(10, 'https://picsum.photos/seed/neo1_b/800/600', 1),
(10, 'https://picsum.photos/seed/neo1_c/800/600', 2),

-- luna (post 11~23)
(11, 'https://picsum.photos/seed/luna1_a/800/600', 0),
(11, 'https://picsum.photos/seed/luna1_b/800/600', 1),

(12, 'https://picsum.photos/seed/luna2_a/800/600', 0),

(13, 'https://picsum.photos/seed/luna3_a/800/600', 0),
(13, 'https://picsum.photos/seed/luna3_b/800/600', 1),

(14, 'https://picsum.photos/seed/luna4_a/800/600', 0),

(15, 'https://picsum.photos/seed/luna5_a/800/600', 0),
(15, 'https://picsum.photos/seed/luna5_b/800/600', 1),
(15, 'https://picsum.photos/seed/luna5_c/800/600', 2),
(15, 'https://picsum.photos/seed/luna5_d/800/600', 3),
(15, 'https://picsum.photos/seed/luna5_e/800/600', 4),

(16, 'https://picsum.photos/seed/luna6_a/800/600', 0),

(17, 'https://picsum.photos/seed/luna7_a/800/600', 0),

-- 최대치 검증용: post 18 은 10장
(18, 'https://picsum.photos/seed/luna8_a/800/600', 0),
(18, 'https://picsum.photos/seed/luna8_b/800/600', 1),
(18, 'https://picsum.photos/seed/luna8_c/800/600', 2),
(18, 'https://picsum.photos/seed/luna8_d/800/600', 3),
(18, 'https://picsum.photos/seed/luna8_e/800/600', 4),
(18, 'https://picsum.photos/seed/luna8_f/800/600', 5),
(18, 'https://picsum.photos/seed/luna8_g/800/600', 6),
(18, 'https://picsum.photos/seed/luna8_h/800/600', 7),
(18, 'https://picsum.photos/seed/luna8_i/800/600', 8),
(18, 'https://picsum.photos/seed/luna8_j/800/600', 9),

(19, 'https://picsum.photos/seed/luna9_a/800/600', 0),

(20, 'https://picsum.photos/seed/luna10_a/800/600', 0),
(20, 'https://picsum.photos/seed/luna10_b/800/600', 1),

(21, 'https://picsum.photos/seed/luna11_a/800/600', 0),

(22, 'https://picsum.photos/seed/luna12_a/800/600', 0),

(23, 'https://picsum.photos/seed/luna13_a/800/600', 0),
(23, 'https://picsum.photos/seed/luna13_b/800/600', 1),

-- zero (post 24)
(24, 'https://picsum.photos/seed/zero1_a/800/600', 0),

-- rain (post 25)
(25, 'https://picsum.photos/seed/rain1_a/800/600', 0);
