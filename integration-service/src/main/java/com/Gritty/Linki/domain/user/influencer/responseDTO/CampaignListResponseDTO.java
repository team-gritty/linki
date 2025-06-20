package com.Gritty.Linki.domain.user.influencer.responseDTO;

import com.Gritty.Linki.vo.enums.CampaignPublishStatus;
import com.Gritty.Linki.vo.enums.Category;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignListResponseDTO {
    private String campaignId;
    private String campaignName;
    private String campaignCondition;
    private String campaignImg;
    private Category campaignCategory;
    private LocalDateTime createdAt;
    private LocalDateTime campaignDeadline;
    private CampaignPublishStatus CampaignPublishStatus;
}
