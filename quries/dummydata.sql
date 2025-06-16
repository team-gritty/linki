-- 기본 사용자 데이터 생성
INSERT INTO `user` (`user_id`, `user_login_id`, `user_login_pw`, `user_name`, `user_phone`, `user_email`, `user_pay_status`, `user_status`, `user_enter_day`, `user_role`)
SELECT 
    CONCAT('USER', LPAD(seq, 4, '0')),
    CONCAT('user', seq),
    '$2a$10$abcdefghijklmnopqrstuvwxyz', -- 암호화된 비밀번호
    CONCAT('사용자', seq),
    CONCAT('010-', LPAD(FLOOR(RAND() * 10000), 4, '0'), '-', LPAD(FLOOR(RAND() * 10000), 4, '0')),
    CONCAT('user', seq, '@example.com'),
    FLOOR(RAND() * 2),
    1,
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    CASE 
        WHEN seq < 500 THEN '인플루언서'
        WHEN seq < 1000 THEN '광고주'
        ELSE '일반유저'
    END
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 + d.N * 1000 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) d
) numbers
WHERE seq < 1500;

-- 인플루언서 데이터 생성 (user_id가 일치하도록)
INSERT INTO `influencer` (`influencer_id`, `user_id`)
SELECT 
    CONCAT('INF', LPAD(seq, 4, '0')),
    CONCAT('USER', LPAD(seq, 4, '0'))
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 500;  -- 인플루언서는 0-499까지

-- 광고주 데이터 생성 (user_id가 일치하도록)
INSERT INTO `advertiser` (`advertiser_id`, `business_number`, `company_name`, `user_id`)
SELECT 
    CONCAT('ADV', LPAD(seq, 4, '0')),
    CONCAT('123-45-', LPAD(seq, 4, '0')),
    CONCAT('회사', seq),
    CONCAT('USER', LPAD(seq + 500, 4, '0'))  -- 500-999까지의 user_id 사용
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 500;  -- 광고주는 0-499까지

-- 채널 데이터 생성
-- 광고주는 0-499까지
-- 채널 데이터 생성
INSERT INTO `channel` (
        `channel_id`,
        `channel_name`,
        `youtube_channel_id`,
        `channel_url`,
        `channel_category`,
        `channel_country`,
        `channel_description`,
        `channel_thumbnail_url`,
        `subscriber_count`,
        `video_count`,
        `view_count`,
        `channel_createdAt`,
        `like_count`,
        `comment_count`,
        `collected_at`,
        `influencer_id`
    )
SELECT CONCAT('CH', LPAD(seq, 4, '0')),
    Merge change Merge change CONCAT('채널', seq),
    CONCAT('UC', LPAD(seq, 22, 'X')),
    -- YouTube 채널 ID 형식 모방
    CONCAT(
        'https://youtube.com/channel/UC',
        LPAD(seq, 22, 'X')
    ),
    CASE
        FLOOR(RAND() * 11)
        WHEN 0 THEN 'BEAUTY'
        WHEN 1 THEN 'FASHION'
        WHEN 2 THEN 'SPORTS'
        WHEN 3 THEN 'FOOD'
        WHEN 4 THEN 'VLOG'
        WHEN 5 THEN 'TRAVEL'
        WHEN 6 THEN 'MUSIC'
        WHEN 7 THEN 'EDUCATION'
        WHEN 8 THEN 'ANIMAL'
        WHEN 9 THEN 'ELECTRONICS'
        ELSE 'ENTERTAINMENT'
    END,
    CASE
        FLOOR(RAND() * 3)
        WHEN 0 THEN '한국'
        WHEN 1 THEN '미국'
        ELSE '일본'
    END,
    CONCAT(
        '이 채널은 ',
        seq,
        '번째 콘텐츠 크리에이터의 채널입니다. 다양한 ',
        CASE
            FLOOR(RAND() * 5)
            WHEN 0 THEN '라이프스타일'
            WHEN 1 THEN '엔터테인먼트'
            WHEN 2 THEN '교육'
            WHEN 3 THEN '게임'
            ELSE '요리'
        END,
        ' 콘텐츠를 제공합니다.'
    ),
    CONCAT(
        'https://yt3.googleusercontent.com/',
        LPAD(seq, 10, '0'),
        '/photo.jpg'
    ),
    FLOOR(RAND() * 9000000) + 1000000,
    -- 구독자 수 (1M-10M)
    FLOOR(RAND() * 900) + 100,
    -- 영상 수 (100-1000)
    FLOOR(RAND() * 900000000) + 100000000,
    -- 조회수 (100M-1B)
    DATE_ADD(
        '2015-01-01',
        INTERVAL FLOOR(
            RAND() * TIMESTAMPDIFF(DAY, '2015-01-01', '2023-12-31')
        ) DAY
    ),
    FLOOR(RAND() * 9000000) + 1000000,
    -- 좋아요 수 (1M-10M)
    FLOOR(RAND() * 900000) + 100000,
    -- 댓글 수 (100K-1M)
    DATE_ADD(
        '2024-01-01',
        INTERVAL FLOOR(
            RAND() * TIMESTAMPDIFF(DAY, '2024-01-01', CURRENT_DATE())
        ) DAY
    ),
    CONCAT('INF', LPAD(FLOOR(seq / 2), 4, '0')) -- 각 인플루언서당 2개의 채널
FROM (
        SELECT a.N + b.N * 10 + c.N * 100 AS seq
        FROM (
                SELECT 0 AS N
                UNION
                SELECT 1
                UNION
                SELECT 2
                UNION
                SELECT 3
                UNION
                SELECT 4
                UNION
                SELECT 5
                UNION
                SELECT 6
                UNION
                SELECT 7
                UNION
                SELECT 8
                UNION
                SELECT 9
            ) a,
            (
                SELECT 0 AS N
                UNION
                SELECT 1
                UNION
                SELECT 2
                UNION
                SELECT 3
                UNION
                SELECT 4
                UNION
                SELECT 5
                UNION
                SELECT 6
                UNION
                SELECT 7
                UNION
                SELECT 8
                UNION
                SELECT 9
            ) b,
            (
                SELECT 0 AS N
                UNION
                SELECT 1
                UNION
                SELECT 2
                UNION
                SELECT 3
                UNION
                SELECT 4
                UNION
                SELECT 5
                UNION
                SELECT 6
                UNION
                SELECT 7
                UNION
                SELECT 8
                UNION
                SELECT 9
            ) c
    ) numbers
WHERE seq < 1000;

-- 캠페인 데이터 생성
INSERT INTO `campaign` (`campaign_id`, `campaign_name`, `campaign_desc`, `campaign_condition`, `campaign_img`, `created_at`, `campaign_deadline`, `campaign_publish_status`, `campaign_category`, `advertiser_id`)
SELECT 
    CONCAT('CAMP', LPAD(seq, 4, '0')),
    CONCAT('캠페인', seq),
    CONCAT('캠페인 설명', seq),
    CONCAT('캠페인 조건', seq),
    CONCAT('https://example.com/images/', seq, '.jpg'),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) + 30 DAY),
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN 'HIDDEN'
        ELSE 'ACTIVE'
    END,
    CASE FLOOR(RAND() * 11)
        WHEN 0 THEN 'BEAUTY'
        WHEN 1 THEN 'FASHION'
        WHEN 2 THEN 'SPORTS'
        WHEN 3 THEN 'FOOD'
        WHEN 4 THEN 'VLOG'
        WHEN 5 THEN 'TRAVEL'
        WHEN 6 THEN 'MUSIC'
        WHEN 7 THEN 'EDUCATION'
        WHEN 8 THEN 'ANIMAL'
        WHEN 9 THEN 'ELECTRONICS'
        ELSE 'ENTERTAINMENT'
    END,
    CONCAT('ADV', LPAD(FLOOR(seq/2), 4, '0'))  -- 각 광고주당 2개의 캠페인
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 제안서 데이터 생성
INSERT INTO `proposal` (`proposal_id`, `contents`, `status`, `submitted_at`, `responded_at`, `influencer_id`, `campaign_id`)
SELECT 
    CONCAT('PROP', LPAD(seq, 4, '0')),
    CONCAT('제안 내용', seq),
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'PENDING'
        WHEN 1 THEN 'ACCEPTED'
        ELSE 'REJECTED'
    END,
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN NULL
        ELSE DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) + 7 DAY)
    END,
    CONCAT('INF', LPAD(FLOOR(seq/2), 4, '0')),  -- 각 인플루언서당 2개의 제안
    CONCAT('CAMP', LPAD(FLOOR(seq/2), 4, '0'))  -- 각 캠페인당 2개의 제안
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 계약 데이터 생성
INSERT INTO `contract` (`contract_id`, `contract_title`, `document_id`, `contract_status`, `contract_start_date`, `contract_end_date`, `contract_amount`, `contract_created_at`, `contract_completed_at`, `contract_payment_date`, `contract_special_terms`, `pdf_file_path`, `ad_delivery_status`, `proposal_id`, `event_type`, `document_name`)
SELECT 
    CONCAT('CONT', LPAD(seq, 4, '0')),
    CONCAT('계약서', seq),
    CONCAT('DOC', LPAD(seq, 4, '0')),
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'PENDING SIGN'
        WHEN 1 THEN 'COMPLETED'
        ELSE 'ONGOING'
    END,
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) + 30 DAY),
    FLOOR(RAND() * 1000000) + 100000,
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) + 7 DAY),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) + 15 DAY),
    CONCAT('특별 조항', seq),
    CONCAT('/contracts/', seq, '.pdf'),
    FLOOR(RAND() * 2),
    CONCAT('PROP', LPAD(seq, 4, '0')),  -- proposal_id와 1:1 매칭
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'VIDEO'
        WHEN 1 THEN 'LIVE'
        ELSE 'POST'
    END,
    CONCAT('계약서_', seq)
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 리다이렉트 링크 데이터 생성
INSERT INTO `redirect_links` (`redirect_id`, `origin_url`, `short_url`, `redirect_url`, `advertiser_id`, `contract_id`)
SELECT 
    CONCAT('RED', LPAD(seq, 4, '0')),
    CONCAT('https://original.com/', seq),
    CONCAT('https://short.link/', seq),
    CONCAT('ADV', LPAD(FLOOR(seq/2), 4, '0')),  -- 각 광고주당 2개의 리다이렉트 링크
    CONCAT('ADV', LPAD(FLOOR(seq/2), 4, '0')),  -- contract_id와 1:1 매칭
    CONCAT('CONT', LPAD(seq, 4, '0'))  -- contract_id와 1:1 매칭
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 리다이렉트 클릭 데이터 생성
INSERT INTO `redirect_click` (`click_id`, `click_time`, `redirect_id`)
SELECT 
    CONCAT('CLICK', LPAD(seq, 4, '0')),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    CONCAT('RED', LPAD(FLOOR(seq/5), 4, '0'))  -- 각 리다이렉트 링크당 5개의 클릭
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 5000;  -- 1000개의 리다이렉트 링크 * 5개의 클릭

-- 채팅방 데이터 생성
INSERT INTO `chat` (`chat_id`, `chat_date`, `chat_status`, `proposal_id`)
SELECT 
    CONCAT('CHA-', LPAD(seq, 16, '0')),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    CASE FLOOR(RAND() * 4)
        WHEN 0 THEN 'PENDING'
        WHEN 1 THEN 'ACTIVE'
        WHEN 2 THEN 'INACTIVE'
        ELSE 'DELETE'
    END,
    CONCAT('PRO-', LPAD(seq, 16, '0'))  -- proposal_id와 1:1 매칭
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;


-- 채팅 알람 데이터 생성
INSERT INTO `chat_alarm` (`chat_alarm_id`, `chat_alarm_is_read`, `chat_alarm_read_at`, `chat_id`)
SELECT 
    CONCAT('ALARM', LPAD(seq, 4, '0')),
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN FALSE
        ELSE TRUE
    END,
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN NULL
        ELSE DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY)
    END,
    CONCAT('CHAT', LPAD(seq, 4, '0'))  -- chat_id와 1:1 매칭
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 서명 데이터 생성
INSERT INTO `signature` (`signature_id`, `signature_signer_name`, `signature_signed_at`, `signature_status`, `contract_id`)
SELECT 
    CONCAT('SIG', LPAD(seq, 4, '0')),
    CONCAT('서명자', seq),
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN NULL
        ELSE DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY)
    END,
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN 'PARTIALLY SIGNED'
        ELSE 'BOTH SIGNED'
    END,
    CONCAT('CONT', LPAD(seq, 4, '0'))  -- contract_id와 1:1 매칭
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 정산 데이터 생성
INSERT INTO `settlement` (`settlement_id`, `settlement_amount`, `settlement_status`, `settlement_date`, `created_at`, `updated_at`, `contract_id`, `influencer_id`)
SELECT 
    CONCAT('SET', LPAD(seq, 4, '0')),
    FLOOR(RAND() * 1000000) + 100000,
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN 'PENDING'
        ELSE 'COMPLETED'
    END,
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN NULL
        ELSE DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY)
    END,
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) + 7 DAY),
    CONCAT('CONT', LPAD(seq, 4, '0')),  -- contract_id와 1:1 매칭
    CONCAT('INF', LPAD(FLOOR(seq/2), 4, '0'))  -- 각 인플루언서당 2개의 정산
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 인플루언서 리뷰 데이터 생성
INSERT INTO `influencer_review` (`influencer_review_id`, `influencer_review_score`, `influencer_review_comment`, `influencer_review_created_at`, `visibility`, `contract_id`)
SELECT 
    CONCAT('REV', LPAD(seq, 4, '0')),
    ROUND(RAND() * 5, 1),
    CONCAT('리뷰 내용', seq),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN FALSE
        ELSE TRUE
    END,
    CONCAT('CONT', LPAD(seq, 4, '0'))  -- contract_id와 1:1 매칭
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 광고주 리뷰 데이터 생성
INSERT INTO `advertiser_review` (`advertiser_review_id`, `advertiser_review_score`, `advertiser_review_comment`, `advertiser_review_created_at`, `visivility`, `contract_id`)
SELECT 
    CONCAT('ADVREV', LPAD(seq, 4, '0')),
    ROUND(RAND() * 5, 1),
    CONCAT('광고주 리뷰 내용', seq),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN FALSE
        ELSE TRUE
    END,
    CONCAT('CONT', LPAD(seq, 4, '0'))  -- contract_id와 1:1 매칭
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 채널 통계 데이터 생성
INSERT INTO `channel_stats` (`stats_id`, `subscriber_count`, `num_of_videos`, `views_per_video`, `data_fetched_at`, `likes_per_video`, `comments_per_video`, `channel_id`)
SELECT 
    CONCAT('STAT', LPAD(seq, 4, '0')),
    FLOOR(RAND() * 1000000),
    FLOOR(RAND() * 1000),
    FLOOR(RAND() * 100000),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    FLOOR(RAND() * 10000),
    FLOOR(RAND() * 1000),
    CONCAT('CH', LPAD(seq, 4, '0'))
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 인플루언서 인증 데이터 생성
INSERT INTO `influencer_auth` (`inf_auth_id`, `inf_auth_token`, `inf_auth_email`, `inf_auth_date`, `channel_id`)
SELECT 
    CONCAT('AUTH', LPAD(seq, 4, '0')),
    CONCAT('token_', seq),
    CONCAT('auth', seq, '@example.com'),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    CONCAT('CH', LPAD(seq, 4, '0'))
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 환불 데이터 생성
INSERT INTO `refund` (`refund_id`, `request_at`, `complete_at`, `canceled_id`, `refund_amount`, `payment_id`, `admin_id`)
SELECT 
    CONCAT('REF', LPAD(seq, 4, '0')),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) + 7 DAY),
    CONCAT('CANCEL', LPAD(seq, 4, '0')),
    FLOOR(RAND() * 1000000) + 100000,
    CONCAT('PAY', LPAD(seq, 4, '0')),
    CONCAT('ADMIN', LPAD(FLOOR(RAND() * 1000), 4, '0'))
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 관리자 데이터 생성
INSERT INTO `admin` (`admin_id`, `admin_login_id`, `admin_login_pw`, `admin_name`, `admin_phone`, `admin_email`, `admin_address`, `admin_enter_day`, `admin_status`)
SELECT 
    CONCAT('ADMIN', LPAD(seq, 4, '0')),
    CONCAT('admin', seq),
    '$2a$10$abcdefghijklmnopqrstuvwxyz', -- 암호화된 비밀번호
    CONCAT('관리자', seq),
    CONCAT('010-', LPAD(FLOOR(RAND() * 10000), 4, '0'), '-', LPAD(FLOOR(RAND() * 10000), 4, '0')),
    CONCAT('admin', seq, '@linki.com'),
    CONCAT('서울시 강남구 테헤란로 ', seq, '길'),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN 0
        ELSE 1
    END
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 구독 데이터 생성
INSERT INTO `subscribe` (`subscribe_id`, `subscribe_code`, `subscribe_amount`, `subscribe_changed_at`, `subscribe_name`)
SELECT 
    CONCAT('SUB', LPAD(seq, 4, '0')),
    CASE 
        WHEN seq < 500 THEN 'InfSub'  -- 0-499: 인플루언서 구독
        ELSE 'adSub'                  -- 500-999: 광고주 구독
    END,
    CASE 
        WHEN seq < 500 THEN 99000     -- 인플루언서 구독료
        ELSE 199000                   -- 광고주 구독료
    END,
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    CASE 
        WHEN seq < 500 THEN '인플루언서 기본 구독'
        ELSE '광고주 프리미엄 구독'
    END
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1000;

-- 결제 ID 데이터 생성 (user_id와 1:1 매칭)
INSERT INTO `billing_id` (`billing_id`, `user_payment_key_billing_key`, `user_patment_key_customer_key`, `user_id`)
SELECT 
    CONCAT('BILL', LPAD(seq, 4, '0')),
    CONCAT('billing_key_', seq),
    CONCAT('customer_key_', seq),
    CONCAT('USER', LPAD(seq, 4, '0'))  -- user_id와 1:1 매칭
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1500;  -- 모든 사용자에 대해 결제 ID 생성

-- 결제 데이터 생성 (billing_id와 subscribe_id 매칭)
INSERT INTO `payments` (`payment_id`, `payed_at`, `payment_method`, `payment_approve_status`, `billing_id`, `subs_detail_id`)
SELECT 
    CONCAT('PAY', LPAD(seq, 4, '0')),
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'CARD'
        WHEN 1 THEN 'BANK'
        ELSE 'VIRTUAL'
    END,
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN FALSE
        ELSE TRUE
    END,
    CONCAT('BILL', LPAD(seq, 4, '0')),  -- billing_id와 1:1 매칭
    CASE 
        WHEN seq < 500 THEN CONCAT('SUB', LPAD(FLOOR(RAND() * 500), 4, '0'))  -- 인플루언서 구독
        WHEN seq < 1000 THEN CONCAT('SUB', LPAD(FLOOR(RAND() * 500) + 500, 4, '0'))  -- 광고주 구독
        ELSE CONCAT('SUB', LPAD(FLOOR(RAND() * 1000), 4, '0'))  -- 일반유저는 랜덤 구독
    END
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 1500;  -- 모든 사용자에 대해 결제 데이터 생성

-- 약관 데이터 생성
INSERT INTO `user_terms` (`terms_id`, `terms_type`, `terms_content`, `terms_version`, `created_at`)
SELECT 
    CONCAT('TERMS', LPAD(seq, 4, '0')),
    CASE 
        WHEN seq % 2 = 0 THEN '이용약관'
        ELSE '개인정보처리방침'
    END,
    CONCAT('약관 내용 버전 ', FLOOR(seq/2) + 1, '.', seq % 2 + 1),
    CONCAT('v', FLOOR(seq/2) + 1, '.', seq % 2 + 1),  -- v1.1, v1.2, v2.1, v2.2 형식
    DATE_ADD('2024-01-01', INTERVAL FLOOR(seq/2) * 30 DAY)  -- 30일 간격으로 버전 업데이트
FROM (
    SELECT a.N + b.N * 10 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b
) numbers
WHERE seq < 20;  -- 10개의 약관 버전 (이용약관 5개, 개인정보처리방침 5개)

-- 약관 동의 데이터 생성
INSERT INTO `user_terms_agreement` (`agreement_id`, `terms_version`, `agreed_at`, `user_id`, `terms_id`)
SELECT 
    CONCAT('AGREE', LPAD(seq, 4, '0')),
    CONCAT('v', FLOOR(seq/2) + 1, '.', seq % 2 + 1),  -- 약관 버전
    DATE_ADD('2024-01-01', INTERVAL FLOOR(RAND() * 365) DAY),  -- 동의 시간
    CONCAT('USER', LPAD(FLOOR(seq/2), 4, '0')),  -- 각 사용자당 2개의 약관 동의
    CASE 
        WHEN seq % 2 = 0 THEN 'TERMS0000'  -- 이용약관
        ELSE 'TERMS0001'  -- 개인정보처리방침
    END
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 AS seq
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
) numbers
WHERE seq < 3000;  -- 1500명의 사용자 * 2개의 약관
