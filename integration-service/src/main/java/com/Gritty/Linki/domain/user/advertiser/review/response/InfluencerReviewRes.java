package com.Gritty.Linki.domain.user.advertiser.review.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 인플루언서에 대한 리뷰 단일 응답
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfluencerReviewRes {
    private String reviewId; // 리뷰 PK
    private BigDecimal reviewScore; // 리뷰 평점
    private String reviewComment; // 리뷰 내용
    private LocalDateTime reviewCreatedAt; // 리뷰 작성 날짜

    private Boolean visibility; // 공개 여부

    // Contract 관련 정보
    private String contractTitle; // 계약 제목
    private LocalDate contractStartDate; // 계약 시작 날짜
    private LocalDate contractEndDate; // 계약 끝 날짜
}