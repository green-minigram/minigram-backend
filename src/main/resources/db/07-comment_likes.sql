-- 07-comment_likes.sql
-- 규칙: 자기 자신의 댓글에 좋아요 X

INSERT INTO comment_likes_tb (comment_id, user_id, created_at)
VALUES
-- post 18의 댓글(1~15)
(1, 3, now()),  -- cos → ssar의 댓글 좋아요
(1, 4, now()),  -- love → ssar의 댓글 좋아요
(1, 5, now()),  -- mango
(2, 2, now()),  -- ssar → cos 댓글 좋아요
(2, 4, now()),  -- love
(3, 2, now()),  -- ssar → mango 댓글 좋아요
(3, 6, now()),  -- hana
(4, 2, now()),  -- ssar → hana 댓글 좋아요
(5, 2, now()),  -- ssar → neo 댓글 좋아요
(5, 3, now()),  -- cos
(6, 2, now()),  -- ssar → luna 답글 좋아요
(7, 2, now()),  -- ssar → love 댓글 좋아요
(8, 5, now()),  -- mango → ssar 대댓글 좋아요
(9, 2, now()),  -- ssar → zero 댓글 좋아요
(10, 3, now()), -- cos → cos 댓글 좋아요
(11, 2, now()), -- ssar → luna 답변 좋아요
(12, 3, now()), -- cos → hana 댓글 좋아요
(13, 2, now()), -- ssar → luna 답변 좋아요
(14, 5, now()), -- mango → neo 댓글 좋아요
(15, 6, now()), -- hana → luna 답변 좋아요

-- post 11의 댓글(16~23)
(16, 3, now()), -- cos → ssar 댓글 좋아요
(17, 2, now()), -- ssar → cos 댓글 좋아요
(18, 5, now()), -- mango → luna 대댓글 좋아요
(19, 7, now()), -- neo → mango 댓글 좋아요
(21, 2, now()), -- ssar → luna 답변 좋아요
(22, 3, now()), -- cos → neo 댓글 좋아요

-- post 3의 댓글(24~28)
(24, 2, now()), -- ssar → neo 댓글 좋아요
(25, 4, now()), -- love → cos 댓글 좋아요
(26, 3, now()), -- cos → ssar 대댓글 좋아요
-- (27, 5, now()), -- mango → mango 댓글 좋아요? (X)
(27, 6, now()), -- hana → mango 댓글 좋아요
(28, 7, now()), -- neo → hana 대댓글 좋아요

-- post 5의 댓글(29~31)
(29, 2, now()), -- ssar → love 댓글 좋아요
(30, 4, now()), -- love → hana 댓글 좋아요
(31, 2, now()), -- ssar → cos 답변 좋아요

-- post 1의 광고 댓글(32~36)
(32, 4, now()), -- love → ssar 댓글 좋아요
(33, 2, now()), -- ssar → cos 댓글 좋아요
(34, 3, now()), -- cos → admin 답글 좋아요
(35, 6, now()), -- hana → mango 댓글 좋아요

-- post 2의 광고 댓글(37~40)
(37, 2, now()), -- ssar → hana 댓글 좋아요
(38, 3, now()), -- cos → neo 댓글 좋아요
(39, 4, now()), -- love → admin 답글 좋아요

-- post 24 (41)
(41, 3, now()), -- cos → ssar 댓글 좋아요

-- post 25 (42)
(42, 2, now()); -- ssar → rain 댓글 좋아요

