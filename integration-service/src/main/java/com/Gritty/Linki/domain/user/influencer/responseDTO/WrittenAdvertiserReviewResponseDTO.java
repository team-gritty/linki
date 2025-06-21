package com.Gritty.Linki.domain.user.influencer.responseDTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WrittenAdvertiserReviewResponseDTO {
    String advertiserReviewId;
    BigDecimal advertiserReviewScore;
    String advertiserReviewComment;
    LocalDateTime advertiserReviewCreatedAt;
    String contractId;
    Boolean visibility;
    String campaignName;
}
