package com.Gritty.Linki.entity;

import com.Gritty.Linki.vo.enums.SignatureStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "signature")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signature {
    @Id
    @Column(name = "signature_id", length = 25, nullable = false)
    private String signatureId; // 전자서명 기록 식별자

    @Column(name = "signature_signer_name", length = 100)
    private String signatureSignerName; // 서명자 이름

    @Column(name = "signature_signed_at")
    private LocalDateTime signatureSignedAt; // 서명 완료 시간

    @Enumerated(EnumType.STRING)
    @Column(name = "signature_status")
    private SignatureStatus signatureStatus; // 서명 상태 (PARTIALLY_SIGNED, BOTH_SIGNED)

    // 🔗 연관 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract; // 연관된 계약서
}
