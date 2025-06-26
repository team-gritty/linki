package com.Gritty.Linki.domain.user.advertiser.review.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 광고주가 작성한 리뷰 조회 응답
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GivenReviewResponse {
    private String reviewId; // 리뷰 PK
    private BigDecimal reviewScore; // 리뷰 평점
    private String reviewComment; // 리뷰 내용
    private LocalDateTime reviewCreatedAt; // 자신이 리뷰 작성한 날짜

    private Boolean visibility; // 공개 여부

    // Contract 관련 정보, 계약 이름이랑 시작 끝 날짜 띄워주기
    private String contractId; // 계약 ID (필터링을 위해 추가)
    private String contractTitle; // 계약 제목
    private LocalDate contractStartDate; // 계약 시작
    private LocalDate contractEndDate; // 계약 끝 날짜

}