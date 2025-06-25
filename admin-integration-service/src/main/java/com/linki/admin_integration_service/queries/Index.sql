use linkiDB;

CREATE INDEX idx_review_contract_id ON advertiser_review(contract_id);
CREATE INDEX idx_contract_proposal_id ON contract(proposal_id);
CREATE INDEX idx_proposal_campaign_id ON proposal(campaign_id);
CREATE INDEX idx_proposal_influencer_id ON proposal(influencer_id);
CREATE INDEX idx_campaign_advertiser_id ON campaign(advertiser_id);
CREATE INDEX idx_advertiser_user_id ON advertiser(user_id);
CREATE INDEX idx_influencer_user_id ON influencer(user_id);
CREATE INDEX idx_channel_influencer_id ON channel(influencer_id);

CREATE INDEX idx_cover_review ON advertiser_review(contract_id, advertiser_review_id, advertiser_review_score, advertiser_review_comment(100));
CREATE INDEX idx_cover_contract ON contract(proposal_id, contract_id);
CREATE INDEX idx_cover_proposal ON proposal(campaign_id, influencer_id, proposal_id);
CREATE INDEX idx_cover_campaign ON campaign(advertiser_id, campaign_id);
CREATE INDEX idx_cover_influencer ON influencer(user_id, influencer_id);
CREATE INDEX idx_cover_channel ON channel(influencer_id, channel_id, channel_name);
CREATE INDEX idx_cover_advertiser ON advertiser(user_id, advertiser_id, company_name);



CREATE INDEX idx_campaign ON campaign(campaign_id);
