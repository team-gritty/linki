package com.Gritty.Linki.domain.user.advertiser.campaign.dto;

import com.Gritty.Linki.vo.enums.CampaignPublishStatus;
import com.Gritty.Linki.vo.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 서비스 로직에서 사용되는 Dto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignDto {

    private String campaignId;
    private String campaignName;
    private String campaignDesc;
    private String campaignCondition;
    private String campaignImg;
    private LocalDateTime createdAt;
    private LocalDateTime campaignDeadline;
    private CampaignPublishStatus campaignPublishStatus;
    private Category campaignCategory;
    private String advertiserId;
    private String companyName;
}