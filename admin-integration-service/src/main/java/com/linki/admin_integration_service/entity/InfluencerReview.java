package com.linki.admin_integration_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "influencer_review")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfluencerReview {

    @Id
    @Column(name = "influencer_review_id", length = 25)
    private String id;

    @Column(name = "influencer_review_score", columnDefinition = "DECIMAL(2,1)")
    private BigDecimal score;

    @Column(name = "influencer_review_comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "influencer_review_created_at")
    private LocalDateTime createdAt;

    @Column(name = "visibility")
    private boolean visibility;

    @Column(name = "contract_id", length = 25)
    private String contractId;


}
