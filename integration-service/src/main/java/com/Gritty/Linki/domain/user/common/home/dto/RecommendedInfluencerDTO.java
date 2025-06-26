package com.Gritty.Linki.domain.user.common.home.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendedInfluencerDTO {

    private String influencerId;
    private String channelId;
    private String influencerName;
    private String userEmail;
    private String channelName;
    private String channelUrl;
    private String channelThumbnailUrl;
    private Long subscriberCount;
    private Long viewCount;
    private String channelCategory;
    private String channelDescription;

    // 점수 관련 정보
    private BigDecimal totalScore;
    private BigDecimal costPerClickScore;
    private BigDecimal dailyTrafficScore;
    private BigDecimal averageReviewScore;
    private BigDecimal contractCountScore;

    // UI 표시용 리뷰 정보
    private BigDecimal displayReviewScore; // 화면에 표시할 리뷰 평점 (1-5점)
    private Long reviewCount; // 리뷰 개수
}