-- 05-comments.sql (for Comment with root_id + parent_id)

INSERT INTO comments_tb (id, post_id, user_id, root_id, parent_id, content, status, created_at, updated_at)
VALUES
-- post 18 (luna 다작 + 최대 이미지 포스트)
(1, 18, 2,  NULL,  NULL, '첫 댓글! 영상 너무 재밌어요 🙌', 'ACTIVE', now(), now()),
(2, 18, 3,  NULL,  NULL, '비하인드도 기대됩니다!', 'ACTIVE', now(), now()),
(3, 18, 5,  NULL,  NULL, '장비 소개 자세히 부탁해요 🔧', 'ACTIVE', now(), now()),
(4, 18, 6,  NULL,  NULL, '편집 고생하셨어요 ✨', 'ACTIVE', now(), now()),
(5, 18, 7,  NULL,  NULL, '썸네일도 예뻐요 😍', 'ACTIVE', now(), now()),
(6, 18, 8,     1,     1, '고마워요! 다음 영상에 넣을게요 🙏', 'ACTIVE', now(), now()),   -- root: 1
(7, 18, 4,     3,     3, '저도 그거 궁금했어요!', 'ACTIVE', now(), now()),              -- root: 3
(8, 18, 2,     2,     2, '222 비하인드 좋아요', 'ACTIVE', now(), now()),                 -- root: 2
(9, 18, 9,     1,     1, '알고리즘 영상도 찍어주세요!', 'ACTIVE', now(), now()),         -- root: 1
(10, 18, 3,    4,     4, '편집툴 뭐 쓰세요?', 'ACTIVE', now(), now()),                   -- root: 4
(11, 18, 8,    4,    10, '프리미어+애프터이펙트 써요', 'ACTIVE', now(), now()),          -- parent:10 → root:4
(12, 18, 6,    5,     5, '썸네일 어떤 앱으로 만들었나요?', 'ACTIVE', now(), now()),       -- root: 5
(13, 18, 8,    5,    12, '포토샵이요! 😊', 'ACTIVE', now(), now()),                        -- parent:12 → root:5
(14, 18, 7,    2,     2, '촬영 셋업 포스팅도 부탁드려요', 'ACTIVE', now(), now()),         -- root: 2
(15, 18, 8,    2,    14, '네, 주말에 정리할게요!', 'ACTIVE', now(), now()),               -- parent:14 → root:2

-- post 11 (luna 브이로그)
(16, 11, 2,  NULL,  NULL, '브이로그 잘 봤어요!', 'ACTIVE', now(), now()),
(17, 11, 3,  NULL,  NULL, '배경음악 제목이 뭐예요?', 'ACTIVE', now(), now()),
(18, 11, 8,    17,    17, '제목은 Sunshine Drive입니다 ☀️', 'ACTIVE', now(), now()),      -- root: 17
(19, 11, 5,  NULL,  NULL, '촬영 장소가 어디인가요?', 'ACTIVE', now(), now()),
(20, 11, 6,    19,    19, '저도 궁금!', 'ACTIVE', now(), now()),                          -- root: 19
(21, 11, 8,    19,    19, '홍대 근처 카페예요', 'ACTIVE', now(), now()),                  -- root: 19
(22, 11, 7,  NULL,  NULL, '영상미 최고…', 'ACTIVE', now(), now()),
(23, 11, 4,  NULL,  NULL, '다음에는 Q&A도 해주세요', 'ACTIVE', now(), now()),

-- post 3 (ssar)
(24, 3,  7,  NULL,  NULL, 'DB 설계 포스팅 깔끔해요', 'ACTIVE', now(), now()),
(25, 3,  3,  NULL,  NULL, 'ERD도 공유해주실 수 있나요?', 'ACTIVE', now(), now()),
(26, 3,  2,    25,    25, '정리해서 올려볼게요!', 'ACTIVE', now(), now()),               -- root: 25
(27, 3,  5,  NULL,  NULL, '정규화 부분 도움됐습니다', 'ACTIVE', now(), now()),
(28, 3,  6,    24,    24, '동감이에요!', 'ACTIVE', now(), now()),                         -- root: 24

-- post 5 (cos)
(29, 5,  4,  NULL,  NULL, '제주 사진 색감 미쳤다…', 'ACTIVE', now(), now()),
(30, 5,  6,  NULL,  NULL, '렌즈 정보 알려주세요!', 'ACTIVE', now(), now()),
(31, 5,  3,    30,    30, '35mm F1.8이에요', 'ACTIVE', now(), now()),                     -- root: 30

-- post 1 (관리자 광고)
(32, 1,  2,  NULL,  NULL, '이벤트 기간이 언제까지인가요?', 'ACTIVE', now(), now()),
(33, 1,  3,  NULL,  NULL, '혜택 자세히 알려주세요', 'ACTIVE', now(), now()),
(34, 1,  1,    33,    33, '자세한 내용은 내일 공지됩니다 📢', 'ACTIVE', now(), now()),    -- root: 33
(35, 1,  5,  NULL,  NULL, '가입 완료했습니다!', 'ACTIVE', now(), now()),
(36, 1,  1,    32,    32, '이번 주 일요일까지예요', 'ACTIVE', now(), now()),             -- root: 32

-- post 2 (관리자 광고)
(37, 2,  6,  NULL,  NULL, '신규 기능 뭐가 추가되나요?', 'ACTIVE', now(), now()),
(38, 2,  7,  NULL,  NULL, '알림 기능 개선 기대합니다', 'ACTIVE', now(), now()),
(39, 2,  1,    37,    37, '스토리 업로드 예약 기능이 추가됩니다', 'ACTIVE', now(), now()), -- root: 37
(40, 2,  8,  NULL,  NULL, '저도 써보고 피드백 드릴게요!', 'ACTIVE', now(), now()),

-- post 24 (zero)
(41, 24, 2,  NULL,  NULL, 'ㅋㅋ 글도 가끔 써주세요', 'ACTIVE', now(), now()),

-- post 25 (rain)
(42, 25, 5,  NULL,  NULL, '첫 글 환영해요!☔', 'ACTIVE', now(), now());

ALTER TABLE comments_tb ALTER COLUMN id RESTART WITH 43;