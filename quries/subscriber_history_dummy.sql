use linkiDB;

-- subscriber_history 테이블을 위한 더미 데이터 생성
-- 각 채널당 7일간의 구독자 수 히스토리 데이터를 생성
-- 1. 기존 subscriber_history 데이터 삭제 (있다면)

-- 2. 각 채널의 현재 구독자 수를 기준으로 7일간 히스토리 생성
INSERT INTO subscriber_history (id, channel_id, subscriber_count, collected_at)
SELECT CONCAT('SH-', LPAD(FLOOR(RAND() * 1000000000), 16, '0')) as id,
    c.channel_id,
    -- 현실적인 구독자 수 변화 로직
    CASE
        -- 첫째 날 (7일 전): 현재 구독자 수의 95-98%
        WHEN day_offset = 0 THEN GREATEST(
            0,
            FLOOR(c.subscriber_count * (0.95 + RAND() * 0.03))
        ) -- 둘째 날 (6일 전): 이전 기준으로 97-100%
        WHEN day_offset = 1 THEN GREATEST(
            0,
            FLOOR(c.subscriber_count * (0.96 + RAND() * 0.04))
        ) -- 셋째 날 (5일 전): 이전 기준으로 97-100%
        WHEN day_offset = 2 THEN GREATEST(
            0,
            FLOOR(c.subscriber_count * (0.97 + RAND() * 0.03))
        ) -- 넷째 날 (4일 전): 이전 기준으로 98-100%
        WHEN day_offset = 3 THEN GREATEST(
            0,
            FLOOR(c.subscriber_count * (0.98 + RAND() * 0.02))
        ) -- 다섯째 날 (3일 전): 이전 기준으로 98-101%
        WHEN day_offset = 4 THEN GREATEST(
            0,
            FLOOR(c.subscriber_count * (0.98 + RAND() * 0.03))
        ) -- 여섯째 날 (2일 전): 이전 기준으로 99-101%
        WHEN day_offset = 5 THEN GREATEST(
            0,
            FLOOR(c.subscriber_count * (0.99 + RAND() * 0.02))
        ) -- 일곱째 날 (1일 전): 현재 구독자 수의 99-100%
        ELSE GREATEST(
            0,
            FLOOR(c.subscriber_count * (0.99 + RAND() * 0.01))
        )
    END as subscriber_count,
    -- 수집 시간: 매일 오전 3시경 (UTC)
    DATE_ADD(
        DATE_SUB(CURDATE(), INTERVAL (6 - day_offset) DAY),
        INTERVAL (3 + FLOOR(RAND() * 2)) HOUR
    ) as collected_at
FROM channel c
    CROSS JOIN (
        SELECT 0 as day_offset -- 7일 전
        UNION
        SELECT 1 -- 6일 전
        UNION
        SELECT 2 -- 5일 전
        UNION
        SELECT 3 -- 4일 전
        UNION
        SELECT 4 -- 3일 전
        UNION
        SELECT 5 -- 2일 전
        UNION
        SELECT 6 -- 1일 전
    ) days
WHERE c.subscriber_count > 0 -- 구독자 수가 0인 채널은 제외
ORDER BY c.channel_id,
    day_offset;
-- 3. 생성된 데이터 확인
SELECT COUNT(*) as total_records,
    COUNT(DISTINCT channel_id) as unique_channels,
    MIN(collected_at) as earliest_date,
    MAX(collected_at) as latest_date
FROM subscriber_history;
-- 4. 샘플 데이터 확인 (한 채널의 7일간 데이터)
SELECT channel_id,
    subscriber_count,
    collected_at,
    DATE(collected_at) as collection_date
FROM subscriber_history
WHERE channel_id = (
        SELECT channel_id
        FROM channel
        WHERE subscriber_count > 0
        LIMIT 1
    )
ORDER BY collected_at;