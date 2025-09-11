-- 08-stories.sql

INSERT INTO stories_tb (user_id, video_url, thumbnail_url, status, created_at, updated_at)
VALUES
-- ssar
(2, 'https://cdn.pixabay.com/video/2015/08/08/125-135736646_large.mp4', 'https://picsum.photos/seed/story01/400/300',
 'ACTIVE', now(), now()),
-- cos
(3, 'https://cdn.pixabay.com/video/2025/06/17/286278_large.mp4', 'https://picsum.photos/seed/story02/400/300',
 'ACTIVE', now(), now()),
-- love
(4, 'https://cdn.pixabay.com/video/2020/01/22/31495-387312407_tiny.mp4', 'https://picsum.photos/seed/story03/400/300',
 'HIDDEN', now(), now()),
-- mango
(5, 'https://cdn.pixabay.com/video/2025/03/28/268290_large.mp4', 'https://picsum.photos/seed/story04/400/300',
 'ACTIVE', now(), now()),
-- hana
(6, 'https://cdn.pixabay.com/video/2025/04/10/271161_large.mp4', 'https://picsum.photos/seed/story05/400/300',
 'HIDDEN', now(), now()),
-- neo
(7, 'https://cdn.pixabay.com/video/2025/02/23/260397_large.mp4', 'https://picsum.photos/seed/story06/400/300',
 'ACTIVE', now(), now()),
-- luna
(8, 'https://cdn.pixabay.com/video/2024/07/13/220945_tiny.mp4', 'https://picsum.photos/seed/story07/400/300',
 'ACTIVE', now(), now()),
-- zero
(9, 'https://cdn.pixabay.com/video/2024/06/17/217115_large.mp4', 'https://picsum.photos/seed/story08/400/300',
 'ACTIVE', now(), now()),
-- rain
(10, 'https://cdn.pixabay.com/video/2019/04/03/22555-328624767_large.mp4', 'https://picsum.photos/seed/story09/400/300',
 'ACTIVE', now(), now()),

-- 데이터 추가
-- ssar (2) 추가 5개 → 총 6+
(2, 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_large.mp4',
 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_tiny.jpg', 'ACTIVE', now(), now()),
(2, 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_large.mp4',
 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_tiny.jpg', 'ACTIVE', now(), now()),
(2, 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_large.mp4',
 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_tiny.jpg', 'HIDDEN', now(), now()),
(2, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'ACTIVE', now(), now()),
(2, 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_large.mp4',
 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_tiny.jpg', 'ACTIVE', now(), now()),

-- cos (3) 추가 4개 → 총 5+
(3, 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_large.mp4',
 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_tiny.jpg', 'ACTIVE', now(), now()),
(3, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'ACTIVE', now(), now()),
(3, 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_large.mp4',
 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_tiny.jpg', 'HIDDEN', now(), now()),
(3, 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_large.mp4',
 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_tiny.jpg', 'ACTIVE', now(), now()),

-- love (4) 추가 6개 → 총 7+
(4, 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_large.mp4',
 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_tiny.jpg', 'ACTIVE', now(), now()),
(4, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'ACTIVE', now(), now()),
(4, 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_large.mp4',
 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_tiny.jpg', 'HIDDEN', now(), now()),
(4, 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_large.mp4',
 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_tiny.jpg', 'ACTIVE', now(), now()),
(4, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'ACTIVE', now(), now()),
(4, 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_large.mp4',
 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_tiny.jpg', 'ACTIVE', now(), now()),

-- mango (5) 추가 3개 → 총 4+
(5, 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_large.mp4',
 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_tiny.jpg', 'ACTIVE', now(), now()),
(5, 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_large.mp4',
 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_tiny.jpg', 'ACTIVE', now(), now()),
(5, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'HIDDEN', now(), now()),

-- hana (6) 추가 4개 → 총 5+
(6, 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_large.mp4',
 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_tiny.jpg', 'ACTIVE', now(), now()),
(6, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'ACTIVE', now(), now()),
(6, 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_large.mp4',
 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_tiny.jpg', 'HIDDEN', now(), now()),
(6, 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_large.mp4',
 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_tiny.jpg', 'ACTIVE', now(), now()),

-- neo (7) 추가 5개 → 총 6+
(7, 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_large.mp4',
 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_tiny.jpg', 'ACTIVE', now(), now()),
(7, 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_large.mp4',
 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_tiny.jpg', 'ACTIVE', now(), now()),
(7, 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_large.mp4',
 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_tiny.jpg', 'HIDDEN', now(), now()),
(7, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'ACTIVE', now(), now()),
(7, 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_large.mp4',
 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_tiny.jpg', 'ACTIVE', now(), now()),

-- luna (8) 추가 6개 → 총 7+
(8, 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_large.mp4',
 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_tiny.jpg', 'ACTIVE', now(), now()),
(8, 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_large.mp4',
 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_tiny.jpg', 'ACTIVE', now(), now()),
(8, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'HIDDEN', now(), now()),
(8, 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_large.mp4',
 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_tiny.jpg', 'ACTIVE', now(), now()),
(8, 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_large.mp4',
 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_tiny.jpg', 'ACTIVE', now(), now()),
(8, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'ACTIVE', now(), now()),

-- zero (9) 추가 3개 → 총 4+
(9, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'ACTIVE', now(), now()),
(9, 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_large.mp4',
 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_tiny.jpg', 'ACTIVE', now(), now()),
(9, 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_large.mp4',
 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_tiny.jpg', 'HIDDEN', now(), now()),

-- rain (10) 추가 5개 → 총 6+
(10, 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_large.mp4',
 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_tiny.jpg', 'ACTIVE', now(), now()),
(10, 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_large.mp4',
 'https://cdn.pixabay.com/video/2016/09/14/5278-182817488_tiny.jpg', 'ACTIVE', now(), now()),
(10, 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_large.mp4',
 'https://cdn.pixabay.com/video/2018/02/19/14385-256955049_tiny.jpg', 'HIDDEN', now(), now()),
(10, 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_large.mp4',
 'https://cdn.pixabay.com/video/2019/02/01/21116-315137080_tiny.jpg', 'ACTIVE', now(), now()),
(10, 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_large.mp4',
 'https://cdn.pixabay.com/video/2016/05/12/3134-166335905_tiny.jpg', 'ACTIVE', now(), now());
