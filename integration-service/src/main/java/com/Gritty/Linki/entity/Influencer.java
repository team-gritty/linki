package com.Gritty.Linki.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
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

    // User와의 연관관계 추가
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * 인플루언서의 이름을 반환 (User 엔티티의 userName 사용)
     *
     * @return 인플루언서 이름
     */
    public String getInfluencerName() {
        return user != null ? user.getUserName() : null;
    }
}
