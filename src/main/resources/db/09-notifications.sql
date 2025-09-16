-- 09-notifications.sql

INSERT INTO notifications_tb (type, sender_id, recipient_id, target_id, status, read_at, created_at, updated_at)
VALUES
-- POST_LIKED: 게시글 좋아요 알림
('POST_LIKED', 3, 2, 7, 'UNREAD', NULL, NOW(), NOW()),   -- cos → ssar (post 3)
('POST_LIKED', 4, 2, 8, 'READ', NOW(), NOW(), NOW()),    -- love → ssar (post 3)
('POST_LIKED', 7, 8, 41, 'UNREAD', NULL, NOW(), NOW()),  -- neo → luna (post 18)
('POST_LIKED', 10, 8, 43, 'READ', NOW(), NOW(), NOW()),  -- rain → luna (post 18)

-- COMMENTED: 게시글에 댓글 작성 알림
('COMMENTED', 2, 8, 1, 'UNREAD', NULL, NOW(), NOW()),    -- ssar가 luna의 post(18)에 댓글(id=1)
('COMMENTED', 3, 8, 2, 'READ', NOW(), NOW(), NOW()),     -- cos가 luna의 post(18)에 댓글(id=2)
('COMMENTED', 5, 2, 24, 'UNREAD', NULL, NOW(), NOW()),   -- mango가 ssar의 post(3)에 댓글(id=24)
('COMMENTED', 6, 3, 30, 'READ', NOW(), NOW(), NOW()),    -- hana가 cos의 post(5)에 댓글(id=30)

-- FOLLOWED: 팔로우 알림
('FOLLOWED', 2, 3, 2, 'UNREAD', NULL, NOW(), NOW()),     -- ssar가 cos를 팔로우
('FOLLOWED', 3, 2, 4, 'READ', NOW(), NOW(), NOW()),      -- cos가 ssar를 팔로우
('FOLLOWED', 9, 8, 15, 'UNREAD', NULL, NOW(), NOW()),    -- zero가 luna를 팔로우

-- STORY_LIKED: 스토리 좋아요 알림
('STORY_LIKED', 3, 2, 4, 'UNREAD', NULL, NOW(), NOW()),  -- cos(3)이 ssar(2)의 스토리(id=1)를 좋아요
('STORY_LIKED', 8, 2, 19, 'READ', NOW(), NOW(), NOW()),  -- luna(8)이 ssar(2)의 스토리(id=1)를 좋아요
('STORY_LIKED', 5, 9, 11, 'UNREAD', NULL, NOW(), NOW()), -- mango(5)이 zero(9)의 스토리(id=8)를 좋아요
('STORY_LIKED', 9, 7, 23, 'UNREAD', NULL, NOW(), NOW()); -- zero(9)이 neo(7)의 스토리(id=6)를 좋아요
