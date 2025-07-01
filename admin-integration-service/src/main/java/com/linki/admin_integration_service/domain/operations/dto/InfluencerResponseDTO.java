package com.linki.admin_integration_service.domain.operations.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InfluencerResponseDTO {
    private String influencerReviewId;
    private String influencer;
    private String writer;
    private String review;
    private BigDecimal rating;
    private LocalDateTime reviewDate;
    private boolean visibility;
    private String contractId;


    public InfluencerResponseDTO toRead(InfluencerReviewDTO influencerReviewDTO) {
        influencerReviewId = influencerReviewDTO.getInfluencerReviewId();
        influencer = influencerReviewDTO.getInfluencer();
        writer = influencerReviewDTO.getWriter();
        review = influencerReviewDTO.getReview();
        rating = influencerReviewDTO.getRating();
        reviewDate = influencerReviewDTO.getReviewDate();
        visibility = influencerReviewDTO.isVisibility();
        contractId = influencerReviewDTO.getContractId();

        return this;
    }

}
