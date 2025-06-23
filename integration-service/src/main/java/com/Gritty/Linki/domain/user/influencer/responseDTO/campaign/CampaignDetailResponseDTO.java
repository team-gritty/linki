package com.Gritty.Linki.domain.user.influencer.responseDTO.campaign;

import com.Gritty.Linki.vo.enums.CampaignPublishStatus;
import com.Gritty.Linki.vo.enums.Category;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignDetailResponseDTO {
    private String campaignId;
    private String campaignName;
    private String campaignCondition;
    private String campaignImg;
    private LocalDateTime createdAt;
    private LocalDateTime campaignDeadline;
    private CampaignPublishStatus CampaignPublishStatus;
    private String campaignDesc;
    private String companyName;
    private Category campaignCategory;

}
