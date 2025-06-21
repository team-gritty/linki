use linkiDB;

DROP TABLE IF EXISTS linki_score;

CREATE TABLE linki_score
(
    score_id VARCHAR(25),
    cost_per_click DECIMAL(10, 2),
    daily_traffic DECIMAL(10, 2),
    average_review_score DECIMAL(10, 2),
    contract_count DECIMAL(10, 2),
    influencer_id VARCHAR(25),
    FOREIGN KEY (influencer_id) REFERENCES influencer(influencer_id)
);
