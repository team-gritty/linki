package com.linki.admin_integration_service.entity;

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

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt; // 제안 제출 시점

    @Column(name = "responded_at")
    private LocalDateTime respondedAt; // 광고주 응답 시점

    // 🔗 연관 관계

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "influencer_id", nullable = false)
    private Influencer influencer; // 제안자 (인플루언서)


}
