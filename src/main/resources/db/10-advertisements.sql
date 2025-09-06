-- 10-advertisements.sql
-- 광고 게시물(post_id=1,2)을 advertisements로 연결

INSERT INTO advertisements_tb (post_id, user_id, status, start_at, end_at, created_at, updated_at)
VALUES
-- 광고 1: 특별 혜택 캠페인 (1주일간 진행)
(1, 1, 'ACTIVE', NOW(), DATEADD('DAY', 7, NOW()), now(), now()),

-- 광고 2: 신규 기능 소개 (향후 2주간 진행 후 종료 예정)
(2, 1, 'ACTIVE', NOW(), DATEADD('DAY', 14, NOW()), now(), now());
