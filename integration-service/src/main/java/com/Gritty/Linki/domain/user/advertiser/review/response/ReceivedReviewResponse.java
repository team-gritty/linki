package com.Gritty.Linki.domain.user.advertiser.review.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 광고주가 받은 리뷰 조회 응답
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedReviewResponse {
    private String reviewId;
    private BigDecimal reviewScore;
    private String reviewComment;
    private LocalDateTime reviewCreatedAt;

    private Boolean visibility; // 공개 여부

    // Contract 관련 정보, 계약 이름이랑 시작 끝 날짜 띄워주기
    // private String contractId;
    private String contractTitle;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;

}