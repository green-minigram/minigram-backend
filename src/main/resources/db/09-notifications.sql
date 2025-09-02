-- 09-notifications.sql

INSERT INTO notifications_tb (type, sender_user_id, recipient_user_id, target_id, status)
VALUES
-- POST_LIKED: 게시글 좋아요 알림
('POST_LIKED', 3, 2, 3, 'UNREAD'),  -- cos가 ssar의 post(3)를 좋아요
('POST_LIKED', 4, 2, 3, 'READ'),    -- love → ssar
('POST_LIKED', 5, 3, 5, 'UNREAD'),  -- mango가 cos의 post(5)를 좋아요
('POST_LIKED', 7, 8, 18, 'UNREAD'), -- neo가 luna 인기글(18)을 좋아요
('POST_LIKED', 10, 8, 18, 'READ'),  -- rain → luna

-- COMMENTED: 게시글에 댓글 작성 알림
('COMMENTED', 2, 8, 1, 'UNREAD'),   -- ssar가 luna의 post(18)에 댓글(id=1)
('COMMENTED', 3, 8, 2, 'READ'),     -- cos가 luna의 post(18)에 댓글(id=2)
('COMMENTED', 5, 2, 24, 'UNREAD'),  -- mango가 ssar의 post(3)에 댓글(id=24)
('COMMENTED', 6, 3, 30, 'READ'),    -- hana가 cos의 post(5)에 댓글(id=30)

-- FOLLOWED: 팔로우 알림
('FOLLOWED', 2, 3, 2, 'UNREAD'),    -- ssar가 cos를 팔로우
('FOLLOWED', 3, 2, 4, 'READ'),      -- cos가 ssar를 팔로우
('FOLLOWED', 9, 8, 15, 'UNREAD'); -- zero가 luna를 팔로우
