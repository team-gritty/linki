package com.linki.admin_integration_service.domain.operations.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdvertiserReviewDTO {
    private String	advertiserReviewId;
    private String advertiser;
    private String writer;
    private String review;
    private BigDecimal rating;
    private LocalDateTime reviewDate;
    private boolean visibility;
    private String contractId;
}
