package com.Gritty.Linki.domain.user.advertiser.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfluencerReviewDto {
    private String reviewId;
    private Integer reviewScore;
    private String reviewComment;
    private LocalDateTime reviewCreatedAt;
    private Boolean visibility;
    private String contractTitle;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
}