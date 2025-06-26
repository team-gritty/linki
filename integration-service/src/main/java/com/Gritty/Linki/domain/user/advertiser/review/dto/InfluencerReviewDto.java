package com.Gritty.Linki.domain.user.advertiser.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 인플루언서에 대한 리뷰 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfluencerReviewDto {
    private String reviewId; // 리뷰 PK
    private Integer reviewScore; // 리뷰 평점
    private String reviewComment; // 리뷰 내용
    private LocalDateTime reviewCreatedAt; // 리뷰 작성 날짜
    private Boolean visibility; // 공개 여부
    private String contractId; // 계약 ID (필터링을 위해 추가)
    private String contractTitle; // 계약 제목
    private LocalDate contractStartDate; // 계약 시작 날짜
    private LocalDate contractEndDate; // 계약 끝 날짜
}