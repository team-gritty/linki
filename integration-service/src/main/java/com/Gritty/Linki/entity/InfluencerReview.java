package com.Gritty.Linki.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "influencer_review")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InfluencerReview {

    @Id
    @Column(name = "influencer_review_id", length = 25)
    private String influencerReviewId;

    @Column(name = "influencer_review_score",  nullable = false, precision = 2, scale = 1)
    private BigDecimal influencerReviewScore;

    @Column(name = "influencer_review_comment", columnDefinition = "TEXT")
    private String influencerReviewComment;

    @Column(name = "influencer_review_created_at")
    private LocalDateTime influencerReviewCreatedAt;

    @Column(name = "visibility", nullable = false)
    private Boolean visibility = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;


}