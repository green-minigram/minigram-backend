-- 12-reports.sql

INSERT INTO reports_tb (type, target_id, reporter_user_id, reason_id, status, created_at, updated_at)
VALUES
-- POST 관련
('POST', 18, 2, 7, 'PENDING', now(), now()),  -- ssar: luna 인기글(18) 스팸/사기 의심 (대기)
('POST', 18, 3, 8, 'REJECTED', now(), now()), -- cos: luna 인기글(18) 거짓 정보 → 기각
('POST', 7, 6, 2, 'APPROVED', now(), now()),  -- hana: love의 글(7) 따돌림/원치 않는 연락 -> 승인 (HIDDEN 처리)
('POST', 3, 7, 9, 'REJECTED', now(), now()),  -- neo: ssar 글(3) 지식재산권 침해 신고 → 기각
('POST', 1, 4, 7, 'REJECTED', now(), now()),  -- love: 관리자 광고(1) 스팸/사기 → 기각
('POST', 24, 3, 8, 'APPROVED', now(), now()), -- cos: zero 글(24) 거짓 정보 -> 승인 (HIDDEN 처리)
('POST', 20, 5, 1, 'REJECTED', now(), now()), -- mango: luna 글(20) 마음에 들지 않음 → 기각

-- STORY 관련
('STORY', 3, 2, 6, 'APPROVED', now(), now()), -- ssar: love 스토리(3) 나체/성적행위 -> 승인 (HIDDEN 처리)
('STORY', 7, 9, 7, 'PENDING', now(), now()),  -- zero: luna 스토리(7) 스팸/사기
('STORY', 1, 4, 2, 'REJECTED', now(), now()), -- love: ssar 스토리(1) 따돌림/원치 않는 연락 → 기각
('STORY', 5, 7, 4, 'APPROVED', now(), now()), -- neo: hana 스토리(5) 폭력/혐오/학대 -> 승인 (HIDDEN 처리)
('STORY', 9, 2, 8, 'PENDING', now(), now()),  -- ssar: rain 스토리(9) 거짓 정보 (대기)

-- 추가 POST 신고 (다양성)
('POST', 11, 10, 5, 'PENDING', now(), now()), -- rain: luna 글(11) 규제 품목 판매/홍보 (대기)
('POST', 15, 9, 2, 'PENDING', now(), now()); -- zero: luna 글(15) 따돌림/원치 않는 연락 (대기)
