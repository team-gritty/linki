package com.Gritty.Linki.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Column(name = "influencer_img")
    private String influencerImg;

    @Column(name = "influencer_intro", columnDefinition = "TEXT")
    private String influencerIntro; // 인플루언서 소개 글

    // User와의 연관관계 추가
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "influencer")
    private List<Channel> channels; // ✅ 여러 개의 채널이라면

    /**
     * 인플루언서의 이름을 반환 (User 엔티티의 userName 사용)
     *
     * @return 인플루언서 이름
     */
    public String getInfluencerName() {
        return user != null ? user.getUserName() : null;
    }
}
