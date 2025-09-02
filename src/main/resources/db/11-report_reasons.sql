-- 11-report_reasons.sql

INSERT INTO report_reasons_tb (id, code, label)
VALUES (1, 'DISLIKE', '마음에 들지 않습니다'),
       (2, 'BULLYING', '따돌림 또는 원치 않는 연락'),
       (3, 'SELF_HARM', '자살, 자해 및 섭식 장애'),
       (4, 'VIOLENCE', '폭력, 혐오 또는 학대'),
       (5, 'ILLEGAL_SALE', '규제 품목을 판매하거나 홍보함'),
       (6, 'NUDITY', '나체 이미지 또는 성적 행위'),
       (7, 'SPAM_SCAM', '스팸, 사기 또는 스팸'),
       (8, 'FALSE_INFO', '거짓 정보'),
       (9, 'IP_INFRINGEMENT', '지식재산권 침해');
