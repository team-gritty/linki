package com.Gritty.Linki.domain.user.advertiser.campaign.response;

import com.Gritty.Linki.vo.enums.CampaignPublishStatus;
import com.Gritty.Linki.vo.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 클라이언트에게 반환되는 Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignResponse {

    private String campaignId; // 캠페인 PK
    private String campaignName;
    private String campaignDesc;
    private String campaignCondition;
    private String campaignImg;
    private LocalDateTime createdAt;
    private LocalDateTime campaignDeadline;
    private CampaignPublishStatus campaignPublishStatus;
    private Category campaignCategory;
    private String advertiserId; // 캠페인을 등록한 광고주 아이디
    private String companyName; // 광고주 회사명 추가
}