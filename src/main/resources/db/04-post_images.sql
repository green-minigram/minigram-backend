-- 04-post_images.sql
-- 규칙: 각 post 최소 1장, 최대 10장
-- URL 예시는 picsum.photos (seed로 고정 재현성 부여)

INSERT INTO post_images_tb (post_id, url)
VALUES
-- 광고글 (post 1~2)
(1, 'https://picsum.photos/seed/ad1_a/800/600'),
(1, 'https://picsum.photos/seed/ad1_b/800/600'),
(2, 'https://picsum.photos/seed/ad2_a/800/600'),

-- ssar (post 3~4)
(3, 'https://picsum.photos/seed/ssar1_a/800/600'),
(3, 'https://picsum.photos/seed/ssar1_b/800/600'),
(4, 'https://picsum.photos/seed/ssar2_a/800/600'),

-- cos (post 5~6)
(5, 'https://picsum.photos/seed/cos1_a/800/600'),
(6, 'https://picsum.photos/seed/cos2_a/800/600'),
(6, 'https://picsum.photos/seed/cos2_b/800/600'),

-- love (post 7)
(7, 'https://picsum.photos/seed/love1_a/800/600'),

-- mango (post 8)
(8, 'https://picsum.photos/seed/mango1_a/800/600'),
(8, 'https://picsum.photos/seed/mango1_b/800/600'),

-- hana (post 9)
(9, 'https://picsum.photos/seed/hana1_a/800/600'),

-- neo (post 10)
(10, 'https://picsum.photos/seed/neo1_a/800/600'),
(10, 'https://picsum.photos/seed/neo1_b/800/600'),
(10, 'https://picsum.photos/seed/neo1_c/800/600'),

-- luna (post 11~23)
(11, 'https://picsum.photos/seed/luna1_a/800/600'),
(11, 'https://picsum.photos/seed/luna1_b/800/600'),

(12, 'https://picsum.photos/seed/luna2_a/800/600'),

(13, 'https://picsum.photos/seed/luna3_a/800/600'),
(13, 'https://picsum.photos/seed/luna3_b/800/600'),

(14, 'https://picsum.photos/seed/luna4_a/800/600'),

(15, 'https://picsum.photos/seed/luna5_a/800/600'),
(15, 'https://picsum.photos/seed/luna5_b/800/600'),
(15, 'https://picsum.photos/seed/luna5_c/800/600'),
(15, 'https://picsum.photos/seed/luna5_d/800/600'),
(15, 'https://picsum.photos/seed/luna5_e/800/600'),

(16, 'https://picsum.photos/seed/luna6_a/800/600'),

(17, 'https://picsum.photos/seed/luna7_a/800/600'),

-- 최대치 검증용: post 18 은 10장
(18, 'https://picsum.photos/seed/luna8_a/800/600'),
(18, 'https://picsum.photos/seed/luna8_b/800/600'),
(18, 'https://picsum.photos/seed/luna8_c/800/600'),
(18, 'https://picsum.photos/seed/luna8_d/800/600'),
(18, 'https://picsum.photos/seed/luna8_e/800/600'),
(18, 'https://picsum.photos/seed/luna8_f/800/600'),
(18, 'https://picsum.photos/seed/luna8_g/800/600'),
(18, 'https://picsum.photos/seed/luna8_h/800/600'),
(18, 'https://picsum.photos/seed/luna8_i/800/600'),
(18, 'https://picsum.photos/seed/luna8_j/800/600'),

(19, 'https://picsum.photos/seed/luna9_a/800/600'),

(20, 'https://picsum.photos/seed/luna10_a/800/600'),
(20, 'https://picsum.photos/seed/luna10_b/800/600'),

(21, 'https://picsum.photos/seed/luna11_a/800/600'),

(22, 'https://picsum.photos/seed/luna12_a/800/600'),

(23, 'https://picsum.photos/seed/luna13_a/800/600'),
(23, 'https://picsum.photos/seed/luna13_b/800/600'),

-- zero (post 24)
(24, 'https://picsum.photos/seed/zero1_a/800/600'),

-- rain (post 25)
(25, 'https://picsum.photos/seed/rain1_a/800/600');
