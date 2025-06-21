package com.Gritty.Linki.domain.user.influencer.requestDTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InfAdvertiserReviewResponseDTO {
 private String advertiserReviewId;
 private BigDecimal advertiserReviewScore;
 private String advertiserReviewComment;
 private LocalDateTime advertiserReviewCreatedAt;
 private String contractId;
 private Boolean visibility;

}
