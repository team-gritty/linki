use linkiDB;

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

SELECT *
FROM influencer_reviews_view;