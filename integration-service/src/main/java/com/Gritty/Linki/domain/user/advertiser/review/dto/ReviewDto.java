package com.Gritty.Linki.domain.user.advertiser.review.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private String reviewId;
    private BigDecimal reviewScore;
    private String reviewComment;
    private LocalDateTime reviewCreatedAt;

    // Contract 관련 정보
    private String contractId;
    private String contractTitle;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private BigDecimal contractAmount;
    private String settlementStatus; // 정산 상태

    // 리뷰 대상 정보
    private String targetId; // 인플루언서 ID 또는 광고주 ID
    private String targetName; // 인플루언서 이름 또는 광고주 이름
}