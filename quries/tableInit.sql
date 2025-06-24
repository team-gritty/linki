DROP DATABASE IF EXISTS linkiDB;
CREATE DATABASE linkiDB;

use linkiDB;

# CREATE USER 'linki'@'%' IDENTIFIED BY 'linki1234';
# GRANT ALL PRIVILEGES ON linkiDB.* TO 'linki'@'%';
# FLUSH PRIVILEGES;

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

DROP TABLE if exists channel_stats;

CREATE TABLE `channel_stats` (
	`stats_id`	VARCHAR(25)	NOT NULL,
	`subscriber_count`	INT	NULL	COMMENT '구독자수',
	`num_of_videos`	INT	NULL	COMMENT '업로드한 총 비디오 개수',
	`views_per_video`	INT	NULL	COMMENT '누적 조회수',
	`data_fetched_at`	DATETIME	NOT NULL	COMMENT '해당 데이터가 수집된 시간(youtube api 기준)',
	`likes_per_video`	INT	NULL	COMMENT '영상 별 좋아요 수',
	`comments_per_video`	INT	NULL	COMMENT '영상 별 댓글 수',
	`channel_id`	VARCHAR(25)	NOT NULL	COMMENT '채널 식별 아이디'
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
	`campaign_condition`	LONGTEXT	NULL	COMMENT '조건 요약(포함 문구, 영상 길이 등)',
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

ALTER TABLE user
    ADD COLUMN user_oauth_provider VARCHAR(20) COMMENT 'google, naver, kakaod 등',
    ADD COLUMN user_oauth_id VARCHAR(50) COMMENT '구글에서 제공하는 식별값',
    ADD COLUMN user_oauth_user BOOLEAN DEFAULT FALSE;

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
DROP TABLE IF EXISTS chat;

CREATE TABLE `chat` (
	`chat_id`	varchar(25)	NOT NULL,
	`chat_date`	datetime	NOT NULL,
	`chat_status`	Enum('PENDING','ACTIVE','INACTIVE','DELETE')	NOT NULL	COMMENT '대기/활성/비활성/삭제',
	`proposal_id`	VARCHAR(25)	NOT NULL	COMMENT '제안서 식별 ID'
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
	`contract_status`	ENUM('PENDING SIGN', 'COMPLETED','ONGOING')	NOT NULL DEFAULT 'PENDING SIGN',
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

ALTER TABLE `channel_stats` ADD CONSTRAINT `PK_CHANNEL_STATS` PRIMARY KEY (
	`stats_id`
);

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

ALTER TABLE `chat` ADD CONSTRAINT `PK_CHAT` PRIMARY KEY (
	`chat_id`
);

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