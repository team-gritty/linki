INSERT INTO user (
    user_id, user_login_id, user_login_pw, user_name, user_phone, user_email,
    user_pay_status, user_status, user_enter_day, user_role
) VALUES
      ('USR-0000000000000001', 'user01', 'pw0001', '고윤아', '010-1111-0001', 'user01@email.com', 1, 1, '2023-01-10', 'ROLE_USER'),
      ('USR-0000000000000002', 'user02', 'pw0002', '김선민', '010-1111-0002', 'seonmink12@naver.com', 1, 1, '2023-02-15', 'ROLE_INFLUENCER'),
      ('USR-0000000000000003', 'user03', 'pw0003', '김성준', '010-1111-0003', 'seonmin.kim1030@gmail.com', 1, 1, '2023-03-20', 'ROLE_INFLUENCER'),
      ('USR-0000000000000004', 'user04', 'pw0004', '신민혁', '010-1111-0004', 'user04@email.com', 1, 0, '2023-04-05', 'ROLE_INFLUENCER'),
      ('USR-0000000000000005', 'user05', 'pw0005', '이정섭', '010-1111-0005', 'user05@email.com', 0, 1, '2023-05-12', 'ROLE_INFLUENCER'),
      ('USR-0000000000000006', 'user06', 'pw0006', '정난희', '010-1111-0006', 'niadev00@email.com', 1, 1, '2023-06-18', 'ROLE_ADVERTISER'),
      ('USR-0000000000000007', 'user07', 'pw0007', '장민호', '010-1111-0007', 'user07@email.com', 1, 0, '2023-07-09', 'ROLE_ADVERTISER'),
      ('USR-0000000000000008', 'user08', 'pw0008', '오서연', '010-1111-0008', 'user08@email.com', 0, 1, '2023-08-23', 'ROLE_ADVERTISER'),
      ('USR-0000000000000009', 'user09', 'pw0009', '한지훈', '010-1111-0009', 'user09@email.com', 1, 1, '2023-09-14', 'ROLE_ADVERTISER'),
      ('USR-0000000000000010', 'user10', 'pw0010', '서지우', '010-1111-0010', 'user10@email.com', 0, 1, '2023-10-30', 'ROLE_ADVERTISER');


INSERT INTO influencer (influencer_id, user_id, influencer_intro, influencer_img) VALUES
                                                                                      ('INF-0000000000000001', 'USR-0000000000000002', '안녕하세요! 인플루언서 선민입니다.', 'https://images.unsplash.com/photo-1603217041431-9a99375beab0?q=80&w=1035&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
                                                                                      ('INF-0000000000000002', 'USR-0000000000000003', '안녕하세요! 인플루언서 성준입니다.', 'https://images.unsplash.com/photo-1613053341085-db794820ce43?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
                                                                                      ('INF-0000000000000003', 'USR-0000000000000004', '안녕하세요! 인플루언서 민혁입니다.', 'https://images.unsplash.com/photo-1680651270398-2eeb8d1b1524?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
                                                                                      ('INF-0000000000000004', 'USR-0000000000000005', '안녕하세요! 인플루언서 정섭입니다.', 'https://images.unsplash.com/photo-1680651270398-2eeb8d1b1524?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D');


INSERT INTO advertiser (advertiser_id, business_number, company_name, user_id) VALUES
                                                                                   ('ADV-0000000000000001', '123-45-6701', '롬앤', 'USR-0000000000000006'),
                                                                                   ('ADV-0000000000000002', '123-45-6702', '센티카', 'USR-0000000000000007'),
                                                                                   ('ADV-0000000000000003', '123-45-6703', '비비고', 'USR-0000000000000008'),
                                                                                   ('ADV-0000000000000004', '123-45-6704', '오뚜기', 'USR-0000000000000009'),
                                                                                   ('ADV-0000000000000005', '123-45-6705', '애플', 'USR-0000000000000010');

INSERT INTO campaign (campaign_id, campaign_name, campaign_desc, campaign_condition, campaign_img, created_at, campaign_deadline, campaign_publish_status, campaign_category, advertiser_id) VALUES
                                                                                                                                                                                                 ('CMP-0000000000000001', '에어팟 신제품 체험', '에어팟 4세대 체험 모집합니다', '조건: 구독자 1만명 이상', 'https://images.unsplash.com/photo-1603351154351-5e2d0600bb77?q=80&w=2071&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', '2025-06-01 10:00:00', '2025-12-22 23:59:59', 'HIDDEN', 'ELECTRONICS', 'ADV-0000000000000005'),
                                                                                                                                                                                                 ('CMP-0000000000000002', '오뚜기 신제품 컵밥 체험', '오뚜기 신제품 마라맛 컵밥 체험 모집합니다', '조건: 이미지 5장 이상 업로드', 'https://media.istockphoto.com/id/1363081253/ko/%EC%82%AC%EC%A7%84/%EB%B2%84%EC%84%AF%EA%B3%BC-%EC%B1%84%EC%86%8C%EB%A5%BC-%EC%A7%81%EC%A0%91-%EA%B3%81%EB%93%A4%EC%9D%B8-%EC%95%84%EC%8B%9C%EC%95%84-%EB%B3%B6%EC%9D%8C%EB%B0%A5-%EC%82%AC%EC%A7%84.jpg?s=1024x1024&w=is&k=20&c=Y5fQ_nI2lDcsqNy_XxMBaI6QG2o-zWpdFAb-ftYsb1g=', '2024-03-01 10:00:00', '2024-12-28 23:59:59', 'ACTIVE', 'FOOD', 'ADV-0000000000000004'),
                                                                                                                                                                                                 ('CMP-0000000000000003', '비비고 만두 체험', '비비고 왕교자 체험 모집합니다', '조건: 구독자 5000명 이상', 'https://img.etoday.co.kr/pto_db/2022/07/20220704102821_1772007_545_665.png', '2024-03-01 10:00:00', '2024-12-22 23:59:59', 'ACTIVE', 'FOOD', 'ADV-0000000000000003'),
                                                                                                                                                                                                 ('CMP-0000000000000004', '센티카 필로우 미스트 체험', '센티카 필로우 미스트 마인드풀 체험 모집합니다.', '조건 업로드 영상 2개 이상', 'https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0022/A00000022654406ko.jpg?qt=80', '2025-06-01 10:00:00', '2025-07-26 23:59:59', 'ACTIVE', 'BEAUTY', 'ADV-0000000000000002'),
                                                                                                                                                                                                 ('CMP-0000000000000005', '비비고 사골곰탕 시식단 모집', '비비고 사골곰탕 시식단을 모집합니다', '조건: 구독자 5000명 이상', 'https://img.cjthemarket.com/images/file/product/771/20240312131546730.jpg?SF=webp', '2025-05-01 10:00:00', '2024-07-10 23:59:59', 'ACTIVE', 'FOOD', 'ADV-0000000000000003'),
                                                                                                                                                                                                 ('CMP-0000000000000006', '롬앤 쥬시 래스팅 틴트 체험단 모집', '롬앤 쥬시 래스팅 틴트 신제품 색상 체험단 모집합니다.', '조건: 뷰티 홍보 경험 필수', 'https://romand.io/images/product/958/iKTjBrzXDwqWW3QimfKxKU8vCSSjeICGlsmvTT3M.jpg', '2025-06-01 10:00:00', '2025-12-17 23:59:59', 'ACTIVE', 'BEAUTY', 'ADV-0000000000000001'),
                                                                                                                                                                                                 ('CMP-0000000000000007', '애플워치 SE 체험단 모집', '애플워치 SE 체험단 모집합니다', '조건: 구독자 10만명 이상', 'https://images.unsplash.com/photo-1551816230-ef5deaed4a26?q=80&w=1065&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', '2025-06-01 10:00:00', '2025-08-13 23:59:59', 'HIDDEN', 'FASHION', 'ADV-0000000000000002'),
                                                                                                                                                                                                 ('CMP-0000000000000008', '센티카 디퓨저 체험단 모집 ', '센티카 대용량 디퓨저 체험단 모집합니다', '조건: 사진 업로드 필수', 'https://sitem.ssgcdn.com/56/99/68/item/1000556689956_i1_750.jpg', '2025-05-01 10:00:00', '2024-07-27 23:59:59', 'ACTIVE', 'BEAUTY', 'ADV-0000000000000005'),
                                                                                                                                                                                                 ('CMP-0000000000000009', '오뚜기 콤비네이션 피자 시식단 모집 ', '오뚜기 콤비네이션 피자 시식단 모집합니다', '조건: 구독자 1000명 이상', 'https://static.megamart.com/product/image/1178/11782988/11782988_1_552.jpg', '2025-03-01 10:00:00', '2025-12-16 23:59:59', 'ACTIVE', 'FOOD', 'ADV-0000000000000004'),
                                                                                                                                                                                                 ('CMP-0000000000000010', '롬앤 섀도우팔레트 체험단 모집', '롬앤 신상 섀도우 팔레트 체험단 모집합니다', '조건: 뷰티 홍보 경험 필수', 'https://cafe24.poxo.com/ec01/romand/6aDrbsrpgztyixM+aENnH1D89vbvN874SJZ0smDxiaa/k9zGF5hClK+Cdcc6Crl70h/a8RobAiR24eeOO4zRMg==/_/web/product/extra/big/202309/692f75dc3841f4f414b5861e1019748d.jpg', '2025-06-01 10:00:00', '2025-08-13 23:59:59', 'ACTIVE', 'BEAUTY', 'ADV-0000000000000001');
INSERT INTO proposal (
    proposal_id, contents, status, submitted_at, responded_at, influencer_id, campaign_id
) VALUES
      ('PRP-0000000000000001', '에어팟 리뷰 영상 제안드립니다.', 'PENDING', '2025-06-20 10:00:00', NULL, 'INF-0000000000000001', 'CMP-0000000000000001'),
      ('PRP-0000000000000002', '컵밥 체험 영상 제작하겠습니다.', 'ACCEPTED', '2025-06-18 12:30:00', '2025-06-20 14:00:00', 'INF-0000000000000002', 'CMP-0000000000000002'),
      ('PRP-0000000000000003', '만두 리뷰 영상 제작 의향 있습니다.', 'REJECTED', '2025-06-17 09:15:00', '2025-06-18 10:00:00', 'INF-0000000000000003', 'CMP-0000000000000003'),
      ('PRP-0000000000000004', '필로우 미스트 상세 리뷰 가능합니다.', 'PENDING', '2025-06-21 11:00:00', NULL, 'INF-0000000000000004', 'CMP-0000000000000004'),
      ('PRP-0000000000000005', '사골곰탕에 대한 콘텐츠 제안드립니다.', 'ACCEPTED', '2025-06-15 16:45:00', '2025-06-16 13:00:00', 'INF-0000000000000001', 'CMP-0000000000000005'),
      ('PRP-0000000000000006', '롬앤 틴트 리뷰 제안드립니다.', 'PENDING', '2025-06-22 08:30:00', NULL, 'INF-0000000000000002', 'CMP-0000000000000006'),
      ('PRP-0000000000000007', '애플워치 SE 체험 리뷰 가능합니다.', 'PENDING', '2025-06-19 10:10:00', NULL, 'INF-0000000000000003', 'CMP-0000000000000007'),
      ('PRP-0000000000000008', '디퓨저 체험 콘텐츠 제작 원합니다.', 'REJECTED', '2025-06-18 13:50:00', '2025-06-19 10:20:00', 'INF-0000000000000004', 'CMP-0000000000000008'),
      ('PRP-0000000000000009', '콤비네이션 피자 리뷰 제안드립니다.', 'ACCEPTED', '2025-06-20 17:00:00', '2025-06-21 09:00:00', 'INF-0000000000000001', 'CMP-0000000000000009'),
      ('PRP-0000000000000010', '섀도우 팔레트 영상 리뷰 가능합니다.', 'PENDING', '2025-06-23 14:25:00', NULL, 'INF-0000000000000002', 'CMP-0000000000000010');


INSERT INTO channel
(channel_id,
 channel_name,
 youtube_channel_id,
 channel_url,
 channel_category,
 channel_country,
 channel_description,
 channel_thumbnail_url,
 subscriber_count,
 video_count,
 view_count,
 like_count,
 comment_count,
 channel_createdAt,
 collected_at,
 influencer_id)
VALUES
    ('CHN-1953599878311485',
     '걷는 여행 Walk Travel',
     'UCeIY89g3KZ6e0HinaOiMHcg',
     'https://www.youtube.com/channel/UCeIY89g3KZ6e0HinaOiMHcg',
     'TRAVEL',
     'KR',
     '대중교통을 이용한 도보 여행 전문 채널입니다.\n\n그럼 한번 걸어볼까요?\n\n문의 : walktraveler@naver.com',
     'https://yt3.ggpht.com/Dcx0T2VmbEhRdGsOT80WGLEXcSztE3cHgf7aXEX62EiMZxza0AttcrDGlmyzLKg6Qbo9o8DHL3c=s88-c-k-c0x00ffffff-no-rj',
     140000,
     127,
     20385034,
     0,
     11557,
     '2023-07-02 05:06:00',
     '2025-06-23 02:10:51',
     'INF-0000000000000001'),
    ('CHN-1953599878772858',
     'KBS여행 걸어서 세계속으로',
     'UCFw4M1BJYYdN1YtS8SzlDzg',
     'https://www.youtube.com/channel/UCFw4M1BJYYdN1YtS8SzlDzg',
     'TRAVEL',
     'KR',
     'This Youtube channel 【Everywhere, K】 is run by Korean Broadcasting System(KBS) TV producers to provide information and enjoyment to travelers all around the world.\nEnjoy our travel clips, covering almost everywhere!\n\nplease contact us at walkingexpedition@gmail.com',
     'https://yt3.ggpht.com/ytc/AIdro_mWiGYsiNCGjiSPm7pdouB4S1_xzn-cdynMbJQgRDbdzc8=s88-c-k-c0x00ffffff-no-rj',
     833000,
     11925,
     354006090,
     0,
     59625,
     '2015-03-25 21:36:02',
     '2025-06-23 02:10:51',
     'INF-0000000000000002'),
    ('CHN-1953599879192289',
     '주피디의 역사여행',
     'UCWPWJFFiJyNvv0Kbq5IzaUg',
     'https://www.youtube.com/channel/UCWPWJFFiJyNvv0Kbq5IzaUg',
     'TRAVEL',
     'KR',
     '안녕하세요 주미영입니다.\n10대 학창시절에는 불과 10여 년 전에 일어난 사건도 호랑이 담배 피우던 시절의 이야기처럼 들렸는데요. 나이가 들고 보니 100년이 훌쩍 지난 사건도 그리 오래전 이야기같지 않습니다.\n어렵고 지루하게만 생각되던 역사가 친근하게 다가오며 호기심이 생기네요.\n\n2022년 3월부터 서울둘레길과 한양도성길을 걸으며 우리 역사에 대해 조금씩 관심을 갖게 되었습니다. 그러다 2023년 3월부터 6개월간 조선왕릉을 탐방했습니다.\n\n저는 라디오 프로듀서로 37년 근무한 KBS에서 2022년 9월 말 정년퇴직하였습니다. 그리고 2023년 2월부터 약 10개월 수련과정을 거쳐  궁해설사도 되었습니다.\n\n앞으로 우리나라 방방곡곡을 여행하며 재미있는 역사 스토리를 여러분께 소개해 드리겠습니다.\n\n부족한 채널이지만 많이 사랑해 주시고 격려해 주시면 감사하겠습니다.\n\n의견 있으시면 언제든 메일로 보내주세요!\n\n이메일 a51515889@gmail.com\nawon2014@naver.com',
     'https://yt3.ggpht.com/erjpTpclaIFkVZ1ys72FHpgOsJw9YTR8Y66L2Dxuqee8DnUFHQaTVteihwPM3y7Hcr83916hj4Y=s88-c-k-c0x00ffffff-no-rj',
     66800,
     183,
     14498678,
     0,
     23973,
     '2022-03-16 00:34:54',
     '2025-06-23 02:10:51',
     'INF-0000000000000003'),
    ('CHN-1953599879611719',
     '세계여행하는 너구리TV',
     'UC37RfRDyeIrSDlGcQS15otg',
     'https://www.youtube.com/channel/UC37RfRDyeIrSDlGcQS15otg',
     'TRAVEL',
     'KR',
     '카톡 ywy9988 해외체류및 여행 25년 경험자의 전세계 걸어서 야외실시간방송입니다^^ 백두산 천지부터 전세계 끝까지!!010 7314 6025, 위쳇 ywy6025',
     'https://yt3.ggpht.com/mC6jC070z4Yis-GWsa6ap_b80Ez1i144j5zx3WVs29_lgO5tIhFSdPPbdi4RO1XLM-JxfwZl_A=s88-c-k-c0x00ffffff-no-rj',
     11300,
     445,
     5416011,
     0,
     0,
     '2020-07-28 18:00:31',
     '2025-06-23 02:10:51',
     'INF-0000000000000004');


INSERT INTO subscriber_history (id, channel_id, subscriber_count, collected_at)
VALUES
-- CHN-1953599878311485 (걷는 여행 Walk Travel)
('SH-0000000000000001', 'CHN-1953599878311485', 135000, '2025-06-17 03:00:00'),
('SH-0000000000000002', 'CHN-1953599878311485', 136000, '2025-06-18 03:00:00'),
('SH-0000000000000003', 'CHN-1953599878311485', 137500, '2025-06-19 03:00:00'),
('SH-0000000000000004', 'CHN-1953599878311485', 138000, '2025-06-20 03:00:00'),
('SH-0000000000000005', 'CHN-1953599878311485', 139000, '2025-06-21 03:00:00'),
('SH-0000000000000006', 'CHN-1953599878311485', 139500, '2025-06-22 03:00:00'),
('SH-0000000000000007', 'CHN-1953599878311485', 140000, '2025-06-23 03:00:00'),

-- CHN-1953599878772858 (KBS여행 걸어서 세계속으로)
('SH-0000000000000008', 'CHN-1953599878772858', 820000, '2025-06-17 03:00:00'),
('SH-0000000000000009', 'CHN-1953599878772858', 823000, '2025-06-18 03:00:00'),
('SH-0000000000000010', 'CHN-1953599878772858', 825000, '2025-06-19 03:00:00'),
('SH-0000000000000011', 'CHN-1953599878772858', 828000, '2025-06-20 03:00:00'),
('SH-0000000000000012', 'CHN-1953599878772858', 830000, '2025-06-21 03:00:00'),
('SH-0000000000000013', 'CHN-1953599878772858', 832000, '2025-06-22 03:00:00'),
('SH-0000000000000014', 'CHN-1953599878772858', 833000, '2025-06-23 03:00:00'),

-- CHN-1953599879192289 (주피디의 역사여행)
('SH-0000000000000015', 'CHN-1953599879192289', 62000, '2025-06-17 03:00:00'),
('SH-0000000000000016', 'CHN-1953599879192289', 63500, '2025-06-18 03:00:00'),
('SH-0000000000000017', 'CHN-1953599879192289', 64000, '2025-06-19 03:00:00'),
('SH-0000000000000018', 'CHN-1953599879192289', 64500, '2025-06-20 03:00:00'),
('SH-0000000000000019', 'CHN-1953599879192289', 65000, '2025-06-21 03:00:00'),
('SH-0000000000000020', 'CHN-1953599879192289', 66000, '2025-06-22 03:00:00'),
('SH-0000000000000021', 'CHN-1953599879192289', 66800, '2025-06-23 03:00:00'),

-- CHN-1953599879611719 (세계여행하는 너구리TV)
('SH-0000000000000022', 'CHN-1953599879611719', 10000, '2025-06-17 03:00:00'),
('SH-0000000000000023', 'CHN-1953599879611719', 10200, '2025-06-18 03:00:00'),
('SH-0000000000000024', 'CHN-1953599879611719', 10400, '2025-06-19 03:00:00'),
('SH-0000000000000025', 'CHN-1953599879611719', 10700, '2025-06-20 03:00:00'),
('SH-0000000000000026', 'CHN-1953599879611719', 11000, '2025-06-21 03:00:00'),
('SH-0000000000000027', 'CHN-1953599879611719', 11200, '2025-06-22 03:00:00'),
('SH-0000000000000028', 'CHN-1953599879611719', 11300, '2025-06-23 03:00:00');

