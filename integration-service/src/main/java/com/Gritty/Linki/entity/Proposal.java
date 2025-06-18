package com.Gritty.Linki.entity;

import com.Gritty.Linki.util.IdGenerator;
import com.Gritty.Linki.vo.enums.ProposalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {
    @Id
    @Column(name = "proposal_id", length = 25, nullable = false)
    private String proposalId; // 제안서 식별 ID

    @Column(name = "contents", columnDefinition = "LONGTEXT")
    private String contents; // 제안 내용

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProposalStatus status; // 제안 상태 (PENDING, ACCEPTED, REJECTED)

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt; // 제안 제출 시점

    @Column(name = "responded_at")
    private LocalDateTime respondedAt; // 광고주 응답 시점

    // 🔗 연관 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_id", nullable = false)
    private Influencer influencer; // 제안자 (인플루언서)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    /**
     * JPA가 DB에 insert하기 직전에 호출되는 메서드
     * 
     * @PrePersist로 내부에서 자동 생성되어 @Id필드가 널이 안되도록 보장함
     */
    @PrePersist
    public void prePersist() {
        if (this.proposalId == null) {
            this.proposalId = IdGenerator.proposalId();
        }
    }
}
