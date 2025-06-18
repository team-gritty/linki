use linkiDB;

DROP VIEW IF EXISTS contract_view;

CREATE VIEW contract_view AS
SELECT
    contract.contract_id AS contractId,
    contract.contract_start_date AS adStartDate,
    contract.contract_end_date AS adEndDate,
    contract.contract_amount AS contractAmount,
    contract.contract_payment_date AS paymentDate,
    CONCAT(channel.channel_name,'(',influencer_user.user_name,')') AS influencerName,
    CONCAT(advertiser.company_name,'(',advertiser_user.user_name,')') AS advertiserName,
    contract.contract_status AS contractStatus,
    contract.pdf_file_path AS contractLink
FROM contract
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
         LEFT JOIN channel
                   ON influencer.influencer_id = channel.influencer_id

select *
from contract_view;

SHOW VARIABLES LIKE 'character_set_database';
SHOW VARIABLES LIKE 'collation_database';