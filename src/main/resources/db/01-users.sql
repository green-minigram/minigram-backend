-- 01-users.sql

INSERT INTO users_tb (email, username, password, role, name, gender, birthdate, profile_image_url, bio)
VALUES
-- 관리자 계정
('admin@minigram.com', 'minigram',
 '$2a$10$FpS0L/H5Te23KLSIqdB2DOoYkpRqvPDs9YubWYNJoMl7I09NzOMEe',
 'ADMIN, USER', '관리자', NULL, NULL, NULL, '미니그램 시스템 관리자'),

-- 일반 유저들
('ssar@nate.com', 'ssar',
 '$2a$10$FpS0L/H5Te23KLSIqdB2DOoYkpRqvPDs9YubWYNJoMl7I09NzOMEe',
 'USER', '쌀', 'MALE', '1995-01-15', 'https://picsum.photos/seed/ssar/200', '백엔드 개발자 지망생'),

('cos@nate.com', 'cos',
 '$2a$10$FpS0L/H5Te23KLSIqdB2DOoYkpRqvPDs9YubWYNJoMl7I09NzOMEe',
 'USER', '코스', 'FEMALE', '1997-03-22', 'https://picsum.photos/seed/cos/200', NULL),

('love@nate.com', 'love',
 '$2a$10$FpS0L/H5Te23KLSIqdB2DOoYkpRqvPDs9YubWYNJoMl7I09NzOMEe',
 'USER', '러브', 'OTHER', NULL, NULL, 'Love wins all'),

('mango@nate.com', 'mango',
 '$2a$10$FpS0L/H5Te23KLSIqdB2DOoYkpRqvPDs9YubWYNJoMl7I09NzOMEe',
 'USER', '망고', 'MALE', '1996-07-07', 'https://picsum.photos/seed/mango/200', '여행 좋아하는 개발자'),

('hana@nate.com', 'hana',
 '$2a$10$FpS0L/H5Te23KLSIqdB2DOoYkpRqvPDs9YubWYNJoMl7I09NzOMEe',
 'USER', '하나', 'FEMALE', '1998-01-02', 'https://picsum.photos/seed/hana/200', '사진과 카페 탐방'),

('neo@nate.com', 'neo',
 '$2a$10$FpS0L/H5Te23KLSIqdB2DOoYkpRqvPDs9YubWYNJoMl7I09NzOMEe',
 'USER', '네오', 'OTHER', NULL, null, '알고리즘 마니아'),

('luna@nate.com', 'luna',
 '$2a$10$FpS0L/H5Te23KLSIqdB2DOoYkpRqvPDs9YubWYNJoMl7I09NzOMEe',
 'USER', '루나', 'FEMALE', '1999-09-09', null, '콘텐츠 크리에이터'),

('zero@nate.com', 'zero',
 '$2a$10$FpS0L/H5Te23KLSIqdB2DOoYkpRqvPDs9YubWYNJoMl7I09NzOMEe',
 'USER', '제로', NULL, NULL, 'https://picsum.photos/seed/zero/200', '관찰자 모드'),

('rain@nate.com', 'rain',
 '$2a$10$FpS0L/H5Te23KLSIqdB2DOoYkpRqvPDs9YubWYNJoMl7I09NzOMEe',
 'USER', '레인', NULL, NULL, 'https://picsum.photos/seed/rain/200', '아직은 조용히');