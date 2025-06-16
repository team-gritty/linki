package com.Gritty.Linki.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "influencer_auth")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InfluencerAuth {
    @Id
    @Column(name = "inf_auth_id", length = 25, nullable = false)
    private String infAuthId; // 인증 고유 ID

    @Column(name = "inf_auth_token")
    private String infAuthToken; // 인증 토큰 (nullable)

    @Column(name = "inf_auth_email")
    private String infAuthEmail; // 인증 이메일 (nullable)

    @Column(name = "inf_auth_date")
    private LocalDateTime infAuthDate; // 인증 일시

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel; // 연관 채널
}
