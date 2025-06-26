package com.Gritty.Linki.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "linki_score")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkiScore {

    @Id
    @Column(name = "score_id")
    private String id;

    @Column(name = "cost_per_click")
    private BigDecimal costPerClick;

    @Column(name = "daily_traffic")
    private BigDecimal dailyTraffic;

    @Column(name = "average_review_score")
    private BigDecimal averageReviewScore;

    @Column(name = "contract_count")
    private BigDecimal contractCount;

    @Column(name = "influencer_id")
    private String influencerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_id", insertable = false, updatable = false)
    private Influencer influencer;

    /**
     * 가중치를 적용한 최종 점수 계산
     * (클릭당 광고단가 점수 × 0.3) + (Daily 유입수 점수 × 0.25) + (리뷰 평점 점수 × 0.25) + (계약 건수 점수 ×
     * 0.2)
     */
    public BigDecimal calculateTotalScore() {
        BigDecimal cpcWeight = costPerClick != null ? costPerClick.multiply(BigDecimal.valueOf(0.3)) : BigDecimal.ZERO;
        BigDecimal trafficWeight = dailyTraffic != null ? dailyTraffic.multiply(BigDecimal.valueOf(0.25))
                : BigDecimal.ZERO;
        BigDecimal reviewWeight = averageReviewScore != null ? averageReviewScore.multiply(BigDecimal.valueOf(0.25))
                : BigDecimal.ZERO;
        BigDecimal contractWeight = contractCount != null ? contractCount.multiply(BigDecimal.valueOf(0.2))
                : BigDecimal.ZERO;

        return cpcWeight.add(trafficWeight).add(reviewWeight).add(contractWeight);
    }
}