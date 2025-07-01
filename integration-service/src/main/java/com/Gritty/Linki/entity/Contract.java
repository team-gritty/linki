package com.Gritty.Linki.entity;

import com.Gritty.Linki.vo.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {
    @Id
    @Column(name = "contract_id", length = 25, nullable = false)
    private String contractId; // 계약 ID

    @Column(name = "contract_title", length = 255, nullable = false)
    private String contractTitle; // 계약 제목

    @Column(name = "document_id", length = 100, nullable = false)
    private String documentId; // 유캔사인 문서 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status", nullable = false)
    private ContractStatus contractStatus; // 계약 상태 (PENDING_SIGN, COMPLETED, ONGOING)

    @Column(name = "contract_start_date", nullable = false)
    private LocalDate contractStartDate; // 캠페인 시작일

    @Column(name = "contract_end_date", nullable = false)
    private LocalDate contractEndDate; // 캠페인 종료일

    @Column(name = "contract_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal contractAmount; // 계약 금액

    @Column(name = "contract_created_at", nullable = false, updatable = false)
    private LocalDateTime contractCreatedAt; // 계약서 생성 시간

    @Column(name = "contract_completed_at")
    private LocalDateTime contractCompletedAt; // 계약서 완료 시간


    @Column(name = "contract_special_terms", columnDefinition = "TEXT")
    private String contractSpecialTerms; // 특약 사항

    @Column(name = "pdf_file_path", length = 255)
    private String pdfFilePath; // PDF 문서 경로

    @Column(name = "ad_delivery_status")
    private Boolean adDeliveryStatus; // 광고 이행 여부

    @Column(name = "event_type", length = 20)
    private String eventType; // 유캔사인 이벤트 타입

    @Column(name = "document_name", length = 50)
    private String documentName; // 유캔사인 문서 이름

    // 연관 관계

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal; // 제안서

    @OneToOne(mappedBy = "contract", fetch = FetchType.LAZY)
    private Settlement settlement;
}
