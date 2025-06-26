package com.Gritty.Linki.domain.user.influencer.responseDTO.home;


import com.Gritty.Linki.vo.enums.CampaignPublishStatus;
import com.Gritty.Linki.vo.enums.Category;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndingTodayCampaignResponseDTO {
    private String campaignId;
    private String campaignName;
    private String campaignCondition;
    private String campaignImg;
    private LocalDateTime createdAt;
    private LocalDateTime campaignDeadline;
    private CampaignPublishStatus campaignPublishStatus;
    private Category campaignCategory;


}
