package com.linki.admin_integration_service.entity;


import com.linki.admin_integration_service.vo.enums.SettlementStatus;
//import com.linki.admin_integration_service.vo.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Settlement {
    @Id
    @Column(name = "settlement_id", length = 25, nullable = false)
    private String settlementId; // 정산 ID

    @Column(name = "settlement_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal settlementAmount; // 정산 금액

    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_status", nullable = false)
    private SettlementStatus settlementStatus; // 정산 상태 (PENDING, COMPLETED)

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate; // 정산 예정일

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일시

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일시

    // 🔗 연관 관계

    @OneToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_id", nullable = false)
    private Influencer influencer; // 정산 받는 인플루언서
}
