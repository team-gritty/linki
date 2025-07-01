use linkiDB;

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