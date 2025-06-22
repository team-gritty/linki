use linkiDB;

DROP TABLE IF EXISTS linki_score;

CREATE TABLE subscriber_history (
    id VARCHAR(25) NOT NULL PRIMARY KEY COMMENT '구독자 히스토리 ID',
    channel_id VARCHAR(25) NOT NULL COMMENT '채널 ID',
    subscriber_count BIGINT NOT NULL COMMENT '구독자 수',
    collected_at DATETIME NOT NULL COMMENT '수집 일시',
    FOREIGN KEY (channel_id) REFERENCES channel(channel_id) -- 채널 테이블의 id를 참조하는 외래키
);

