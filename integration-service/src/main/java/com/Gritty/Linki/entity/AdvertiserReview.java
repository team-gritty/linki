package com.Gritty.Linki.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "advertiser_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertiserReview {
    @Id
    @Column(name = "advertiser_review_id", nullable = false, length = 255)
    private String advertiserReviewId;

    @Column(name = "advertiser_review_score", nullable = false, precision = 2, scale = 1)
    private BigDecimal advertiserReviewScore;

    @Column(name = "advertiser_review_comment", columnDefinition = "TEXT")
    private String advertiserReviewComment;

    @Column(name = "advertiser_review_created_at", nullable = false)
    private LocalDateTime advertiserReviewCreatedAt;

    @Column(name = "visivility", nullable = false)
    // 빌더 패턴 사용 시 기본값 설정
    @Builder.Default
    private Boolean visibility = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;
}
