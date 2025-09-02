-- 07-comment_likes.sql
-- 규칙: 자기 자신의 댓글에 좋아요 X

INSERT INTO comment_likes_tb (comment_id, user_id)
VALUES
-- post 18의 댓글(1~15)
(1, 3),  -- cos → ssar의 댓글 좋아요
(1, 4),  -- love → ssar의 댓글 좋아요
(1, 5),  -- mango
(2, 2),  -- ssar → cos 댓글 좋아요
(2, 4),  -- love
(3, 2),  -- ssar → mango 댓글 좋아요
(3, 6),  -- hana
(4, 2),  -- ssar → hana 댓글 좋아요
(5, 2),  -- ssar → neo 댓글 좋아요
(5, 3),  -- cos
(6, 2),  -- ssar → luna 답글 좋아요
(7, 2),  -- ssar → love 댓글 좋아요
(8, 5),  -- mango → ssar 대댓글 좋아요
(9, 2),  -- ssar → zero 댓글 좋아요
(10, 3), -- cos → cos 댓글 좋아요
(11, 2), -- ssar → luna 답변 좋아요
(12, 3), -- cos → hana 댓글 좋아요
(13, 2), -- ssar → luna 답변 좋아요
(14, 5), -- mango → neo 댓글 좋아요
(15, 6), -- hana → luna 답변 좋아요

-- post 11의 댓글(16~23)
(16, 3), -- cos → ssar 댓글 좋아요
(17, 2), -- ssar → cos 댓글 좋아요
(18, 5), -- mango → luna 대댓글 좋아요
(19, 7), -- neo → mango 댓글 좋아요
(21, 2), -- ssar → luna 답변 좋아요
(22, 3), -- cos → neo 댓글 좋아요

-- post 3의 댓글(24~28)
(24, 2), -- ssar → neo 댓글 좋아요
(25, 4), -- love → cos 댓글 좋아요
(26, 3), -- cos → ssar 대댓글 좋아요
-- (27, 5), -- mango → mango 댓글 좋아요? (X)
(27, 6), -- hana → mango 댓글 좋아요
(28, 7), -- neo → hana 대댓글 좋아요

-- post 5의 댓글(29~31)
(29, 2), -- ssar → love 댓글 좋아요
(30, 4), -- love → hana 댓글 좋아요
(31, 2), -- ssar → cos 답변 좋아요

-- post 1의 광고 댓글(32~36)
(32, 4), -- love → ssar 댓글 좋아요
(33, 2), -- ssar → cos 댓글 좋아요
(34, 3), -- cos → admin 답글 좋아요
(35, 6), -- hana → mango 댓글 좋아요

-- post 2의 광고 댓글(37~40)
(37, 2), -- ssar → hana 댓글 좋아요
(38, 3), -- cos → neo 댓글 좋아요
(39, 4), -- love → admin 답글 좋아요

-- post 24 (41)
(41, 3), -- cos → ssar 댓글 좋아요

-- post 25 (42)
(42, 2); -- ssar → rain 댓글 좋아요
