package com.Gritty.Linki.domain.user.influencer.responseDTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedInfluencerReviewResponseDTO {
    String influencerReviewId;
    BigDecimal influencerReviewScore;
    String influencerReviewComment;
    LocalDateTime influencerReviewCreatedAt;
    String contractId;
    Boolean visibility;
    String campaignName;

}
