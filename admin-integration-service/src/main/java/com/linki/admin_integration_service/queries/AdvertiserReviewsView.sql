use linkiDB;

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

select *
from advertiser_reviews_view