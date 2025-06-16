package com.Gritty.Linki.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "influencer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Influencer {
    @Id
    @Column(name = "influencer_id", length = 25, nullable = false)
    private String influencerId; // 인플루언서 식별 ID

    @Column(name = "user_id", length = 25, nullable = false)
    private String userId; // 일반 유저 테이블과 연동되는 회원 ID (UUID or 소셜 PK)
}
