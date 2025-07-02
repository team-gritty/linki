DROP DATABASE IF EXISTS linkiDB;
CREATE DATABASE linkiDB;

use linkiDB;

CREATE USER 'linki'@'%' IDENTIFIED BY 'linki1234';
GRANT ALL PRIVILEGES ON linkiDB.* TO 'linki'@'%';
FLUSH PRIVILEGES;

DROP TABLE IF EXISTS refund;

CREATE TABLE `refund` (
                          `refund_id`	VARCHAR(25)	NOT NULL,
                          `request_at`	DATE	NOT NULL,
                          `complete_at`	DATE	NOT NULL,
                          `canceled_id`	VARCHAR(255)	NOT NULL,
                          `refund_amount`	INTEGER	NOT NULL,
                          `payment_id`	VARCHAR(25)	NOT NULL,
                          `admin_id`	VARCHAR(255)	NOT NULL	COMMENT 'UUID or 스노우플레이크'
);

DROP TABLE IF EXISTS influencer;

CREATE TABLE `influencer` (
                              `influencer_id`	varchar(25)	NOT NULL	COMMENT '인플루언서 식별 아이디',
                              `user_id`	VARCHAR(25)	NOT NULL	COMMENT 'UUID or 스노우플레이크',
                              `influencer_intro` LONGTEXT NULL COMMENT '인플루언서 자기 소개',
                              `influencer_img` LONGTEXT NULL COMMENT '인플루언서 프로필 사진'
);

DROP TABLE IF EXISTS linki_score;

CREATE TABLE linki_score
(
    score_id VARCHAR(25) primary key ,
    cost_per_click DECIMAL(10, 2),
    daily_traffic DECIMAL(10, 2),
    average_review_score DECIMAL(10, 2),
    contract_count DECIMAL(10, 2),
    influencer_id VARCHAR(25)
);


DROP TABLE IF EXISTS settlement;

CREATE TABLE `settlement` (
                              `settlement_id`	VARCHAR(25)	NOT NULL,
                              `settlement_amount`	DECIMAL(15,2)	NOT NULL,
                              `settlement_status`	ENUM('PENDING','COMPLETED')	NOT NULL	COMMENT 'PENDING, COMPLETED',
                              `settlement_date`	DATE	NULL,
                              `created_at`	TIMESTAMP	NOT NULL,
                              `updated_at`	TIMESTAMP	NOT NULL,
                              `contract_id`	VARCHAR(25)	NOT NULL,
                              `influencer_id`	varchar(25)	NOT NULL COMMENT '인플루언서 식별 아이디'
);

DROP TABLE IF EXISTS advertiser_review;

CREATE TABLE `advertiser_review` (
                                     `advertiser_review_id`	VARCHAR(25)	NOT NULL,
                                     `advertiser_review_score`	DECIMAL(2,1)	NOT NULL,
                                     `advertiser_review_comment`	TEXT	NULL,
                                     `advertiser_review_created_at`	DATETIME	NOT NULL,
                                     `visibility`	BOOLEAN	NOT NULL	DEFAULT TRUE,
                                     `contract_id`	VARCHAR(25)	NOT NULL
);

DROP TABLE IF EXISTS redirect_links;

CREATE TABLE `redirect_links` (
                                  `redirect_id`	VARCHAR(25)	NOT NULL,
                                  `origin_url`	LONGTEXT	NOT NULL,
                                  `short_url`	LONGTEXT	NULL,
                                  `redirect_url`	LONGTEXT	NOT NULL,
                                  `advertiser_id`	varchar(25)	NOT NULL,
                                  `contract_id`	VARCHAR(25)	NOT NULL
);

DROP TABLE IF EXISTS user_terms_agreement;

CREATE TABLE `user_terms_agreement` (
                                        `agreement_id`	VARCHAR(25)	NOT NULL	COMMENT '약관 동의 식별 ID',
                                        `terms_version`	VARCHAR(30)	NOT NULL	COMMENT '약관 Version',
                                        `agreed_at`	DATETIME	NOT NULL	COMMENT '회원이 약관 동의한 시간',
                                        `user_id`	VARCHAR(25)	NOT NULL	COMMENT 'UUID or 스노우플레이크',
                                        `terms_id`	VARCHAR(25)	NOT NULL	COMMENT '약관 식별 ID'
);

DROP TABLE IF EXISTS payments;

CREATE TABLE `payments` (
                            `payment_id`	VARCHAR(25)	NOT NULL,
                            `payed_at`	DATE	NOT NULL,
                            `payment_method`	VARCHAR(25)	NOT NULL,
                            `payment_approve_status`	BOOLEAN	NOT NULL,
                            `billing_id`	VARCHAR(25)	NOT NULL,
                            `subs_detail_id`	VARCHAR(25)	NOT NULL
);

DROP TABLE IF EXISTS subscribe;

CREATE TABLE `subscribe` (
                             `subscribe_id`	VARCHAR(25)	NOT NULL,
                             `subscribe_code`	enum('InfSub','adSub')	NOT NULL,
                             `subscribe_amount`	INTEGER	NOT NULL,
                             `subscribe_changed_at`	DATE	NOT NULL,
                             `subscribe_name`	VARCHAR(25)	NOT NULL
);

DROP TABLE IF EXISTS notice_view;

CREATE TABLE `notice_view` (
                               `notice_view_id`	VARCHAR(25)	NOT NULL,
                               `notice_id`	VARCHAR(255)	NOT NULL,
                               `notice_view_log`	INTEGER	NOT NULL	DEFAULT 1	COMMENT '게시글 누를떄마다 레코드 쌓아서 연산해서 보여줄지?  한 레코드에 숫자 업데이트 할지 정해야함'
);

DROP TABLE IF EXISTS user_terms;

CREATE TABLE `user_terms` (
                              `terms_id`	VARCHAR(25)	NOT NULL	COMMENT '약관 식별 ID',
                              `terms_type`	VARCHAR(25)	NOT NULL	COMMENT '약관 종류(이용약관, 개인정보처리방침)',
                              `terms_content`	LONGTEXT	NOT NULL	COMMENT '약관 내용',
                              `terms_version`	VARCHAR(30)	NOT NULL	COMMENT '약관 버전',
                              `created_at`	DATETIME	NOT NULL	COMMENT '약관 변경 시간'
);

DROP TABLE IF EXISTS campaign;

CREATE TABLE `campaign` (
                            `campaign_id`	VARCHAR(25)	NOT NULL	COMMENT '제품 식별 ID',
                            `campaign_name`	VARCHAR(100)	NOT NULL	COMMENT '제품의 이름',
                            `campaign_desc`	LONGTEXT	NULL	COMMENT '제품 설명',
                            `campaign_condition`	LONGTEXT	NOT NULL	COMMENT '조건 요약(포함 문구, 영상 길이 등)',
                            `campaign_img`	LONGTEXT	NOT NULL	COMMENT '광고 제품 이미지',
                            `created_at`	DATETIME	NOT NULL	COMMENT '등록일',
                            `campaign_deadline`	DATETIME	NOT NULL	COMMENT '광고 신청  마감일',
                            `campaign_publish_status`	ENUM('HIDDEN','ACTIVE')	NULL,
                            `campaign_category` ENUM(
                                'BEAUTY', 'FASHION', 'SPORTS', 'FOOD', 'VLOG', 'TRAVEL',
                                'MUSIC', 'EDUCATION', 'ANIMAL', 'ELECTRONICS', 'ENTERTAINMENT'
                                ) NOT NULL ,
                            `advertiser_id`	VARCHAR(25)	NOT NULL	COMMENT '광고 등록한 광고주'
);

DROP TABLE IF EXISTS  signature;

CREATE TABLE `signature` (
                             `signature_id`	VARCHAR(25)	NOT NULL,
                             `signature_signer_name`	VARCHAR(100)	NULL,
                             `signature_signed_at`	TIMESTAMP	NULL,
                             `signature_status`	ENUM('PARTIALLY SIGNED', 'BOTH SIGNED')	NULL,
                             `contract_id`	VARCHAR(25)	NOT NULL
);

DROP TABLE IF EXISTS user;

CREATE TABLE `user` (
                        `user_id`	VARCHAR(25)	NOT NULL,
                        `user_login_id`	VARCHAR(20),
                        `user_login_pw`	VARCHAR(255),
                        `user_name`	VARCHAR(20)	NOT NULL,
                        `user_phone`	VARCHAR(15),
                        `user_email`	VARCHAR(50)	NOT NULL,
                        `user_pay_status`	INTEGER	NULL,
                        `user_status`	INTEGER	NOT NULL DEFAULT 1,
                        `user_enter_day`	DATE	NOT NULL,
                        `user_role`	VARCHAR(20)	NOT NULL	COMMENT '일반유자,인플루언서,광고주',
                        `user_oauth_provider` VARCHAR(20) COMMENT 'google, naver, kakaod 등',
                        `user_oauth_id` VARCHAR(50) COMMENT '구글에서 제공하는 식별값',
                        `user_oauth_user` boolean DEFAULT  FALSE
);

DROP TABLE IF EXISTS qna_comment;

CREATE TABLE `qna_comment` (
                               `qna_comment_id`	VARCHAR(25)	NOT NULL,
                               `qna_id`	VARCHAR(255)	NOT NULL,
                               `qna_comment_content`	LONGTEXT	NOT NULL,
                               `admin_id`	VARCHAR(255)	NOT NULL,
                               `qna_created_at`	DATETIME	NOT NULL,
                               `qna_id2`	VARCHAR(25)	NOT NULL
);

DROP TABLE IF EXISTS proposal;

CREATE TABLE `proposal` (
                            `proposal_id`	VARCHAR(25)	NOT NULL	COMMENT '제안서 식별 ID',
                            `contents`	LONGTEXT	NULL	COMMENT '광고 제안 내용',
                            `status`	ENUM('PENDING','ACCEPTED','REJECTED')	NOT NULL	COMMENT 'PENDING, ACCEPTED, REJECTED (대기중, 수락됨, 거절됨)',
                            `submitted_at`	DATETIME	NOT NULL	COMMENT '제안 제출 시점',
                            `responded_at`	DATETIME	NULL	COMMENT '광고주 응답 시점',
                            `influencer_id`	VARCHAR(25)	NOT NULL	COMMENT '제안자',
                            `campaign_id`	VARCHAR(25)	NOT NULL	COMMENT '캠페인 ID'
);

DROP TABLE IF EXISTS jwt_refresh_token;

CREATE TABLE `jwt_refresh_token` (
                                     `jwt_id`	VARCHAR(25)	NOT NULL	COMMENT '토큰 id',
                                     `refresh_token`	LONGTEXT	NOT NULL	COMMENT '리프레시 토큰',
                                     `created_at`	DATETIME	NOT NULL	COMMENT '리프레시 토큰 생성일'
);

DROP TABLE IF EXISTS notice;

CREATE TABLE `notice` (
                          `notice_id`	VARCHAR(25)	NOT NULL,
                          `notice_title`	VARCHAR(255)	NOT NULL,
                          `notice_content`	LONGTEXT	NULL,
                          `notice_writer`	VARCHAR(255)	NOT NULL,
                          `notice_created_at`	DATETIME	NOT NULL,
                          `notice_status`	TINYINT	NOT NULL	DEFAULT 1	COMMENT '1 = 게시 , 0 = 미게시'
);

DROP TABLE IF EXISTS advertiser;

CREATE TABLE `advertiser` (
                              `advertiser_id`	varchar(25)	NOT NULL,
                              `business_number`	VARCHAR(20)	NOT NULL,
                              `company_name`	VARCHAR(30)	NOT NULL,
                              `user_id`	VARCHAR(25)	NOT NULL
);

DROP TABLE IF EXISTS channel;

CREATE TABLE `channel` (
                           `channel_id` VARCHAR(25) NOT NULL COMMENT '채널 식별 아이디',
                           `channel_name` VARCHAR(255) NOT NULL COMMENT '채널명',
                           `youtube_channel_id` VARCHAR(255) NOT NULL COMMENT 'YouTube 채널 ID',
                           `channel_url` VARCHAR(255) NOT NULL COMMENT '채널 URL',
                           `channel_category` VARCHAR(100) NOT NULL COMMENT '채널 카테고리',
                           `channel_country` VARCHAR(100) NOT NULL COMMENT '채널 국가',
                           `channel_description` TEXT NULL COMMENT '채널 설명',
                           `channel_thumbnail_url` VARCHAR(255) NULL COMMENT '채널 썸네일 URL',
                           `subscriber_count` BIGINT NULL COMMENT '구독자 수',
                           `video_count` INT NULL COMMENT '동영상 수',
                           `view_count` BIGINT NULL COMMENT '조회수',
                           `like_count` BIGINT NULL COMMENT '좋아요 수',
    -- 바로 안될수도
                           `comment_count` BIGINT NULL COMMENT '댓글 수',
    -- 바로 안될수도
                           `channel_createdAt` DATETIME NOT NULL COMMENT '채널 생성 일자',
                           `collected_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 수집 시간',
                           `influencer_id` varchar(25) NOT NULL COMMENT '인플루언서 식별 아이디'
);


drop table if exists subscriber_history;

CREATE TABLE subscriber_history (
                                    id VARCHAR(25) NOT NULL PRIMARY KEY COMMENT '구독자 히스토리 ID',
                                    channel_id VARCHAR(25) NOT NULL COMMENT '채널 ID',
                                    subscriber_count BIGINT NOT NULL COMMENT '구독자 수',
                                    collected_at DATETIME NOT NULL COMMENT '수집 일시'
);


DROP TABLE IF EXISTS redirect_click;

CREATE TABLE `redirect_click` (
                                  `click_id`	VARCHAR(25)	NOT NULL,
                                  `click_time`	DATETIME	NULL,
                                  `redirect_id`	VARCHAR(25)	NOT NULL
);

DROP TABLE IF EXISTS qna_board;

CREATE TABLE `qna_board` (
                             `qna_id`	VARCHAR(25)	NOT NULL,
                             `qna_title`	VARCHAR(255)	NOT NULL,
                             `qna_content`	LONGTEXT	NULL,
                             `qna_writer`	VARCHAR(255)	NOT NULL,
                             `qna_status`	TINYINT	NOT NULL	DEFAULT 0	COMMENT '답변 여부 status'
);

DROP TABLE IF EXISTS billing_id;

CREATE TABLE `billing_id` (
                              `billing_id`	VARCHAR(255)	NOT NULL,
                              `user_payment_key_billing_key`	VARCHAR(100)	NOT NULL,
                              `user_patment_key_customer_key`	VARCHAR(100)	NOT NULL,
                              `user_id`	VARCHAR(25)	NOT NULL
);

DROP TABLE IF EXISTS chat_alarm;

CREATE TABLE `chat_alarm` (
                              `chat_alarm_id`	varchar(25)	NOT NULL,
                              `chat_alarm_is_read`	boolean	NOT NULL,
                              `chat_alarm_read_at`	datetime	NULL,
                              `chat_id`	varchar(25)	NOT NULL
);

DROP TABLE IF EXISTS contract;

CREATE TABLE `contract` (
                            `contract_id`	VARCHAR(25)	NOT NULL,
                            `contract_title`	VARCHAR(255)	NOT NULL,
                            `document_id`	VARCHAR(100)	NOT NULL,
                            `contract_status`	ENUM('PENDING_SIGN', 'COMPLETED','ONGOING')	NOT NULL DEFAULT 'PENDING_SIGN',
                            `contract_start_date`	DATE	NOT NULL,
                            `contract_end_date`	DATE	NOT NULL,
                            `contract_amount`	DECIMAL(15,2)	NOT NULL,
                            `contract_created_at`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                            `contract_completed_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `contract_payment_date`	DATE	NOT NULL,
                            `contract_special_terms`	TEXT	NULL,
                            `pdf_file_path`	VARCHAR(255)	NULL,
                            `ad_delivery_status`	TINYINT	NULL,
                            `proposal_id`	VARCHAR(25)	NOT NULL	COMMENT '제안서 식별 ID',
                            `event_type`	VARCHAR(20)	NULL,
                            `document_name`	VARCHAR(50)	NULL,
                            `ad_delivery_url`	VARCHAR(100) NULL COMMENT '광고 이행 url'

);

DROP TABLE IF EXISTS influencer_auth;

CREATE TABLE `influencer_auth` (
                                   `inf_auth_id`	VARCHAR(25)	NOT NULL,
                                   `inf_auth_token`	VARCHAR(255)	NULL,
                                   `inf_auth_email`	VARCHAR(255)	NULL,
                                   `inf_auth_date`	VARCHAR(255)	NULL,
                                   `channel_id`	VARCHAR(25)	NOT NULL	COMMENT '채널 식별 아이디'
);

DROP TABLE IF EXISTS influencer_review;

CREATE TABLE `influencer_review` (
                                     `influencer_review_id`	VARCHAR(25)	NOT NULL	COMMENT '인플루언서 리뷰 식별 아이디',
                                     `influencer_review_score`	DECIMAL(2,1)	NOT NULL	COMMENT '평점(ex.4.5)',
                                     `influencer_review_comment`	TEXT	NULL	COMMENT '코멘트내용(자유 서술형)',
                                     `influencer_review_created_at`	DATETIME	NULL	COMMENT '리뷰 작성일자',
                                     `visibility`	BOOLEAN	NOT NULL	DEFAULT TRUE,
                                     `contract_id`	VARCHAR(25)	NOT NULL	COMMENT '어떤 계약에 대한 평가인지'
);

DROP TABLE IF EXISTS admin;

CREATE TABLE `admin` (
                         `admin_id`	VARCHAR(25)	NOT NULL,
                         `admin_login_id`	VARCHAR(20)	NOT NULL,
                         `admin_login_pw`	VARCHAR(255)	NOT NULL,
                         `admin_name`	VARCHAR(20)	NOT NULL,
                         `admin_phone`	VARCHAR(15)	NOT NULL,
                         `admin_email`	VARCHAR(50)	NOT NULL,
                         `admin_address`	VARCHAR(255)	NOT NULL,
                         `admin_enter_day`	DATE	NOT NULL,
                         `admin_status`	TINYINT	NOT NULL	DEFAULT 0
);

ALTER TABLE `refund` ADD CONSTRAINT `PK_REFUND` PRIMARY KEY (
                                                             `refund_id`
    );

ALTER TABLE `influencer` ADD CONSTRAINT `PK_INFLUENCER` PRIMARY KEY (
                                                                     `influencer_id`
    );

ALTER TABLE `settlement` ADD CONSTRAINT `PK_SETTLEMENT` PRIMARY KEY (
                                                                     `settlement_id`
    );

ALTER TABLE `advertiser_review` ADD CONSTRAINT `PK_ADVERTISER_REVIEW` PRIMARY KEY (
                                                                                   `advertiser_review_id`
    );

ALTER TABLE `redirect_links` ADD CONSTRAINT `PK_REDIRECT_LINKS` PRIMARY KEY (
                                                                             `redirect_id`
    );

ALTER TABLE `user_terms_agreement` ADD CONSTRAINT `PK_USER_TERMS_AGREEMENT` PRIMARY KEY (
                                                                                         `agreement_id`
    );

ALTER TABLE `payments` ADD CONSTRAINT `PK_PAYMENTS` PRIMARY KEY (
                                                                 `payment_id`
    );

ALTER TABLE `subscribe` ADD CONSTRAINT `PK_SUBSCRIBE` PRIMARY KEY (
                                                                   `subscribe_id`
    );


# ALTER TABLE `subscriber_history` ADD CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channel(channel_id);

ALTER TABLE `notice_view` ADD CONSTRAINT `PK_NOTICE_VIEW` PRIMARY KEY (
                                                                       `notice_view_id`
    );

ALTER TABLE `user_terms` ADD CONSTRAINT `PK_USER_TERMS` PRIMARY KEY (
                                                                     `terms_id`
    );

ALTER TABLE `campaign` ADD CONSTRAINT `PK_CAMPAIGN` PRIMARY KEY (
                                                                 `campaign_id`
    );

ALTER TABLE `signature` ADD CONSTRAINT `PK_SIGNATURE` PRIMARY KEY (
                                                                   `signature_id`
    );

ALTER TABLE `user` ADD CONSTRAINT `PK_USER` PRIMARY KEY (
                                                         `user_id`
    );

ALTER TABLE `qna_comment` ADD CONSTRAINT `PK_QNA_COMMENT` PRIMARY KEY (
                                                                       `qna_comment_id`
    );

ALTER TABLE `proposal` ADD CONSTRAINT `PK_PROPOSAL` PRIMARY KEY (
                                                                 `proposal_id`
    );

ALTER TABLE `jwt_refresh_token` ADD CONSTRAINT `PK_JWT_REFRESH_TOKEN` PRIMARY KEY (
                                                                                   `jwt_id`
    );

ALTER TABLE `notice` ADD CONSTRAINT `PK_NOTICE` PRIMARY KEY (
                                                             `notice_id`
    );

# ALTER TABLE `chat` ADD CONSTRAINT `PK_CHAT` PRIMARY KEY (
#                                                          `chat_id`
#     );

ALTER TABLE `advertiser` ADD CONSTRAINT `PK_ADVERTISER` PRIMARY KEY (
                                                                     `advertiser_id`
    );

ALTER TABLE `channel` ADD CONSTRAINT `PK_CHANNEL` PRIMARY KEY (
                                                               `channel_id`
    );

ALTER TABLE `redirect_click` ADD CONSTRAINT `PK_REDIRECT_CLICK` PRIMARY KEY (
                                                                             `click_id`
    );

ALTER TABLE `qna_board` ADD CONSTRAINT `PK_QNA_BOARD` PRIMARY KEY (
                                                                   `qna_id`
    );

ALTER TABLE `billing_id` ADD CONSTRAINT `PK_BILLING_ID` PRIMARY KEY (
                                                                     `billing_id`
    );

ALTER TABLE `chat_alarm` ADD CONSTRAINT `PK_CHAT_ALARM` PRIMARY KEY (
                                                                     `chat_alarm_id`
    );

ALTER TABLE `contract` ADD CONSTRAINT `PK_CONTRACT` PRIMARY KEY (
                                                                 `contract_id`
    );

ALTER TABLE `influencer_auth` ADD CONSTRAINT `PK_INFLUENCER_AUTH` PRIMARY KEY (
                                                                               `inf_auth_id`
    );

ALTER TABLE `influencer_review` ADD CONSTRAINT `PK_INFLUENCER_REVIEW` PRIMARY KEY (
                                                                                   `influencer_review_id`
    );

ALTER TABLE `admin` ADD CONSTRAINT `PK_ADMIN` PRIMARY KEY (
                                                           `admin_id`
    );

ALTER TABLE admin MODIFY COLUMN admin_status ENUM('PENDING','AGREEMENT','REJECTED') NOT NULL DEFAULT 'PENDING';

ALTER TABLE redirect_links DROP COLUMN short_url;

ALTER TABLE `linki_score`
    ADD CONSTRAINT fk_influencer_id
        FOREIGN KEY (influencer_id) REFERENCES influencer(influencer_id);


# ALTER TABLE `linki_score`
#     ADD CONSTRAINT fk_influencer_id
#         FOREIGN KEY (influencer_id) REFERENCES influencer(influencer_id);

ALTER TABLE `subscriber_history` ADD CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channel(channel_id);


ALTER TABLE `redirect_click` ADD COLUMN click_count INTEGER;
ALTER TABLE `redirect_click` MODIFY COLUMN click_time DATE;


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



INSERT INTO admin (admin_id, admin_login_id, admin_login_pw, admin_name, admin_phone, admin_email, admin_address, admin_enter_day, admin_status) VALUES
                 ('ADM-0000000000000001','test','$2a$10$NTevE0HUBD5vbFAV4rjJXeHes1pfXkw9AVgsMWFiUVo.ascC/uD4O',
                  '테스트','010-0000-0000','linki.noreply@gmail.com','서울 강남구 봉은사로 501','2023-06-13','AGREEMENT');



DROP VIEW IF EXISTS advertiser_reviews_view;

CREATE VIEW advertiser_reviews_view AS
SELECT
    review.advertiser_review_id AS advertiserReviewId,
    CONCAT(advertiser.company_name,'(',advertiser_user.user_name,')') AS advertiser,
    CONCAT(channel.channel_name,'(',influencer_user.user_name,')') AS writer,
    review.advertiser_review_score AS rating,
    review.advertiser_review_created_at AS reviewDate,
    review.visibility AS visibility,
    review.advertiser_review_comment AS review,
    review.contract_id AS contractId
FROM advertiser_review review
         JOIN contract
              ON review.contract_id = contract.contract_id
         JOIN proposal
              ON contract.proposal_id = proposal.proposal_id
         JOIN campaign
              ON proposal.campaign_id = campaign.campaign_id
         JOIN advertiser
              ON campaign.advertiser_id = advertiser.advertiser_id
         JOIN user AS advertiser_user
              ON advertiser.user_id = advertiser_user.user_id
         JOIN influencer
              ON proposal.influencer_id = influencer.influencer_id
         JOIN user AS influencer_user
              ON influencer.user_id = influencer_user.user_id
         JOIN channel
              ON channel.influencer_id = influencer.influencer_id;


DROP VIEW IF EXISTS contract_view;

CREATE VIEW contract_view AS
SELECT
    contract.contract_id AS contractId,
    contract.contract_start_date AS adStartDate,
    contract.contract_end_date AS adEndDate,
    contract.contract_amount AS contractAmount,
    contract.contract_payment_date AS paymentDate,
    CONCAT(channel.channel_name,'(',influencer_user.user_name,')') AS influencerName,
    CONCAT(advertiser.company_name,'(',advertiser_user.user_name,')') AS advertiserName,
    contract.contract_status AS contractStatus,
    contract.pdf_file_path AS contractLink
FROM contract
         JOIN proposal
              ON contract.proposal_id = proposal.proposal_id
         JOIN campaign
              ON proposal.campaign_id = campaign.campaign_id
         JOIN advertiser
              ON campaign.advertiser_id = advertiser.advertiser_id
         JOIN user AS advertiser_user
              ON advertiser.user_id = advertiser_user.user_id
         JOIN influencer
              ON proposal.influencer_id = influencer.influencer_id
         JOIN user AS influencer_user
              ON influencer.user_id = influencer_user.user_id
         LEFT JOIN (
    SELECT influencer_id, MIN(channel_name) AS channel_name
    FROM channel
    GROUP BY influencer_id
) AS channel ON influencer.influencer_id = channel.influencer_id;



DROP VIEW IF EXISTS influencer_reviews_view;

CREATE VIEW influencer_reviews_view AS
SELECT
    review.influencer_review_id AS influencerReviewId,
    CONCAT(channel.channel_name,'(',influencer_user.user_name,')') AS influencer,
    CONCAT(advertiser.company_name,'(',advertiser_user.user_name,')') AS writer,
    review.influencer_review_score AS rating,
    review.influencer_review_created_at AS reviewDate,
    review.visibility AS visibility,
    review.influencer_review_comment AS review,
    review.contract_id AS contractId
FROM influencer_review review
         JOIN contract
              ON review.contract_id = contract.contract_id
         JOIN proposal
              ON contract.proposal_id = proposal.proposal_id
         JOIN influencer
              ON proposal.influencer_id = influencer.influencer_id
         JOIN user AS influencer_user
              ON influencer.user_id = influencer_user.user_id
         JOIN campaign
              ON proposal.campaign_id = campaign.campaign_id
         JOIN advertiser
              ON advertiser.advertiser_id = campaign.advertiser_id
         JOIN user AS advertiser_user
              ON advertiser.user_id = advertiser_user.user_id
         LEFT JOIN channel
                   ON channel.influencer_id = influencer.influencer_id;



-- ==========================================
-- 필수 인덱스만 (최소화)
-- ==========================================

-- 기본 외래키 인덱스 (필수)
CREATE INDEX idx_contract_proposal_id ON contract(proposal_id);
CREATE INDEX idx_proposal_campaign_id ON proposal(campaign_id);
CREATE INDEX idx_proposal_influencer_id ON proposal(influencer_id);
CREATE INDEX idx_campaign_advertiser_id ON campaign(advertiser_id);
CREATE INDEX idx_advertiser_user_id ON advertiser(user_id);
CREATE INDEX idx_influencer_user_id ON influencer(user_id);
CREATE INDEX idx_channel_influencer_id ON channel(influencer_id);

-- Keyset 페이지네이션용 (가장 중요)
CREATE INDEX idx_user_role_id_keyset ON user(user_role, user_id);
CREATE INDEX idx_contract_id_order ON contract(contract_id);

-- ==========================================
-- Advertiser Reviews View 최적화용 인덱스
-- ==========================================

-- Review 기본 인덱스 (Keyset 페이지네이션용)
CREATE INDEX idx_advertiser_review_id_order ON advertiser_review(advertiser_review_id);

-- Review 관련 인덱스
CREATE INDEX idx_advertiser_review_contract_id ON advertiser_review(contract_id);

-- Influencer Review 최적화용 인덱스
CREATE INDEX idx_influencer_review_id_order ON influencer_review(influencer_review_id);
CREATE INDEX idx_influencer_review_contract_id ON influencer_review(contract_id);