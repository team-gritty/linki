package com.linki.admin_integration_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "advertiser_review")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertiserReview {

    @Id
    @Column(name = "advertiser_review_id", length = 25)
    private String id;

    @Column(name = "advertiser_review_score", columnDefinition = "DECIMAL(2,1)")
    private BigDecimal score;

    @Column(name = "advertiser_review_comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "advertiser_review_created_at")
    private LocalDateTime createdAt;

    @Column(name = "visibility")
    private boolean visibility;

    @Column(name = "contract_id", length = 25)
    private String contractId;


}
