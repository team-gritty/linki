package com.linki.admin_integration_service.domain.operations.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdvertiserResponseDTO {
    private String	advertiserReviewId;
    private String advertiser;
    private String writer;
    private String review;
    private BigDecimal rating;
    private LocalDateTime reviewDate;
    private boolean visibility;
    private String contractId;


    public AdvertiserResponseDTO toRead(AdvertiserReviewDTO advertiserReviewDTO) {
        advertiserReviewId = advertiserReviewDTO.getAdvertiserReviewId();
        advertiser = advertiserReviewDTO.getAdvertiser();
        writer = advertiserReviewDTO.getWriter();
        review = advertiserReviewDTO.getReview();
        rating = advertiserReviewDTO.getRating();
        reviewDate = advertiserReviewDTO.getReviewDate();
        visibility = advertiserReviewDTO.isVisibility();
        contractId = advertiserReviewDTO.getContractId();

        return this;
    }

}
