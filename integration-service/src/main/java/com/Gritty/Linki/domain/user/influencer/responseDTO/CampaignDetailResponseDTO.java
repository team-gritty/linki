package com.Gritty.Linki.domain.user.influencer.responseDTO;

import lombok.*;

import java.time.LocalDate;

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
    private LocalDate createdAt;
    private LocalDate campaignDeadline;
    private Enum CampaignPublishStatus;
    private String campaignDesc;
    private String companyName;
}
