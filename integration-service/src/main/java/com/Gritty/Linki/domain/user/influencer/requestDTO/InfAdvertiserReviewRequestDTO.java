package com.Gritty.Linki.domain.user.influencer.requestDTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfAdvertiserReviewRequestDTO {
    private String contractId;
    private BigDecimal advertiserReviewScore;
    private String advertiserReviewComment;

}
