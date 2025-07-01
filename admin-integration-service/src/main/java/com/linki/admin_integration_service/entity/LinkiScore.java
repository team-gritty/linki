package com.linki.admin_integration_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "linki_score")
@Data
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

}
