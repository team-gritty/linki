-- 방법 1: TRUNCATE 사용 (더 빠르고 안전)
TRUNCATE TABLE `influencer_review`;
TRUNCATE TABLE `advertiser_review`;

-- 새로운 더미 데이터 삽입
-- 인플루언서 리뷰 데이터 생성 (광고주에 대한 평가)
INSERT INTO `influencer_review` (`influencer_review_id`, `influencer_review_score`, `influencer_review_comment`, `influencer_review_created_at`, `visibility`, `contract_id`)
SELECT
    CONCAT('IRV-', LPAD(seq, 16, '0')),
    CASE
        WHEN seq % 20 = 0 THEN ROUND(1.0 + RAND() * 2.0, 1)  -- 5% 낮은 점수 (1.0-3.0)
        WHEN seq % 10 = 0 THEN ROUND(2.5 + RAND() * 1.0, 1)  -- 10% 중간 점수 (2.5-3.5)
        ELSE ROUND(4.0 + RAND() * 1.0, 1)                    -- 85% 높은 점수 (4.0-5.0)
        END,
    CASE
        WHEN seq % 20 = 0 THEN
            CASE FLOOR(RAND() * 3)
                WHEN 0 THEN '정산이 너무 늦게 들어와서 불편했어요. 다음에는 좀 더 빠르게 처리해주시면 좋겠습니다.'
                WHEN 1 THEN '협업 과정에서 소통이 부족했던 것 같아요. 요구사항을 명확하게 전달해주시면 더 좋은 결과물을 만들 수 있을 것 같습니다.'
                ELSE '계약 조건과 실제 진행 과정에서 차이가 있어서 당황스러웠어요. 앞으로는 더 정확한 정보를 제공해주세요.'
                END
        WHEN seq % 10 = 0 THEN
            CASE FLOOR(RAND() * 3)
                WHEN 0 THEN '전반적으로 괜찮은 협업이었어요. 정산도 적절한 시기에 들어왔고, 소통도 원활했습니다.'
                WHEN 1 THEN '협업 과정에서 몇 가지 개선할 점이 있었지만, 전반적으로는 만족스러웠습니다.'
                ELSE '무난한 협업이었습니다. 다음에도 기회가 있다면 함께 일하고 싶어요.'
                END
        ELSE
            CASE FLOOR(RAND() * 4)
                WHEN 0 THEN '정말 만족스러운 협업이었어요! 정산도 빠르고 소통도 원활해서 다음에도 함께 일하고 싶습니다.'
                WHEN 1 THEN '광고주분이 너무 친절하고 전문적이셨어요. 요구사항도 명확하고 정산도 정확하게 해주셔서 감사했습니다.'
                WHEN 2 THEN '전체적으로 매우 만족스러운 프로젝트였습니다. 다음 기회에도 꼭 함께 일하고 싶어요!'
                ELSE '광고주분이 요구사항을 잘 전달해주셔서 작업하기 편했어요. 정산도 약속한 대로 해주셨습니다.'
                END
        END,
    DATE_ADD('2023-01-01', INTERVAL FLOOR(RAND() * TIMESTAMPDIFF(DAY, '2023-01-01', '2025-05-31')) DAY),
    CASE FLOOR(RAND() * 10)
        WHEN 0 THEN FALSE  -- 10% 비공개
        ELSE TRUE
        END,
    CONCAT('CTR-', LPAD(seq, 16, '0'))
FROM (
         SELECT a.N + b.N * 10 + c.N * 100 AS seq
         FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
              (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
              (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
     ) numbers
WHERE seq < 1000;

-- 광고주 리뷰 데이터 생성 (인플루언서에 대한 평가)
INSERT INTO `advertiser_review` (`advertiser_review_id`, `advertiser_review_score`, `advertiser_review_comment`, `advertiser_review_created_at`, `visibility`, `contract_id`)
SELECT
    CONCAT('ARV-', LPAD(seq, 16, '0')),
    CASE
        WHEN seq % 20 = 0 THEN ROUND(1.0 + RAND() * 2.0, 1)  -- 5% 낮은 점수 (1.0-3.0)
        WHEN seq % 10 = 0 THEN ROUND(2.5 + RAND() * 1.0, 1)  -- 10% 중간 점수 (2.5-3.5)
        ELSE ROUND(4.0 + RAND() * 1.0, 1)                    -- 85% 높은 점수 (4.0-5.0)
        END,
    CASE
        WHEN seq % 20 = 0 THEN
            CASE FLOOR(RAND() * 3)
                WHEN 0 THEN '광고 효과가 기대에 훨씬 못 미쳤어요. 콘텐츠 품질도 낮고 답변도 늦어서 실망스러웠습니다.'
                WHEN 1 THEN '계약 조건을 제대로 지키지 않아서 문제가 많았어요. 다음에는 더 신중하게 선택하겠습니다.'
                ELSE '소통이 전혀 안 되고 작업물도 마감일에 맞춰서 제출해서 스트레스가 많았습니다.'
                END
        WHEN seq % 10 = 0 THEN
            CASE FLOOR(RAND() * 3)
                WHEN 0 THEN '전반적으로 만족스러운 협업이었어요. 광고 효과도 기대한 수준이었고, 소통도 원활했습니다.'
                WHEN 1 THEN '무난하게 잘 해주셨습니다. 다음에도 기회가 있다면 함께 일하고 싶어요.'
                ELSE '작업물 퀄리티는 좋았는데, 답변이 조금 늦었던 점이 아쉬웠어요. 전반적으로는 만족합니다.'
                END
        ELSE
            CASE FLOOR(RAND() * 4)
                WHEN 0 THEN '정말 훌륭한 인플루언서였어요! 광고 효과도 기대 이상이고, 소통도 원활해서 다음에도 꼭 함께 일하고 싶습니다.'
                WHEN 1 THEN '전문적이고 창의적인 콘텐츠를 만들어주셔서 매출 증대에 큰 도움이 되었어요. 정말 감사합니다!'
                WHEN 2 THEN '답변도 빠르고 작업물도 퀄리티가 높아서 만족스러웠습니다. 추천합니다!'
                ELSE '협업 과정에서 몇 가지 개선할 점이 있었지만, 결과물은 만족스러웠습니다.'
                END
        END,
    DATE_ADD('2023-01-01', INTERVAL FLOOR(RAND() * TIMESTAMPDIFF(DAY, '2023-01-01', '2025-05-31')) DAY),
    CASE FLOOR(RAND() * 10)
        WHEN 0 THEN FALSE  -- 10% 비공개
        ELSE TRUE
        END,
    CONCAT('CTR-', LPAD(seq, 16, '0'))
FROM (
         SELECT a.N + b.N * 10 + c.N * 100 AS seq
         FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
              (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
              (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
     ) numbers
WHERE seq < 1000;