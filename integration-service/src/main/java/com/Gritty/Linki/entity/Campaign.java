package com.Gritty.Linki.entity;

import com.Gritty.Linki.util.IdGenerator;
import com.Gritty.Linki.vo.enums.CampaignPublishStatus;
import com.Gritty.Linki.vo.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campaign")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 본 생성자는 protected 접근 제어자로 생성됨 (JPA에서 프록시 등을 위해 필요)
@AllArgsConstructor
@Builder
public class Campaign {

    // 캠페인 고유 식별자. null 불가.
    @Id
    @Column(name = "campaign_id", length = 25)
    private String campaignId;

    // 캠페인 이름. null 불가.
    @Column(name = "campaign_name", length = 100, nullable = false)
    private String campaignName;

    // 캠페인 설명. null 불가.
    @Column(name = "campaign_desc", columnDefinition = "LONGTEXT")
    private String campaignDesc;

    // 캠페인 조건. null 가능
    @Column(name = "campaign_condition", columnDefinition = "LONGTEXT")
    private String campaignCondition;

    // 이미지 URL 혹은 base64. null 불가.
    @Column(name = "campaign_img", columnDefinition = "LONGTEXT", nullable = false)
    private String campaignImg;

    // 캠페인 (등록)생성 시간. 자동 생성됨. null 불가.
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 캠페인 마감 시간. null 불가.
    @Column(name = "campaign_deadline", nullable = false)
    private LocalDateTime campaignDeadline;

    // 열거형 상태 (문자열로 저장). 공개 여부.
    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_publish_status")
    private CampaignPublishStatus campaignPublishStatus;

    // 캠페인 카테고리. null 불가.
    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_category", nullable = false)
    private Category campaignCategory;

    // 연관관계 매핑
    // Advertiser와 다대일 관계. 지연 로딩 사용. null 불가.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertiser_id", nullable = false)
    private Advertiser advertiser;

    // Proposal와 일대다 관계. 캠페인 삭제 시 모든 제안서 삭제.
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    // 빌더 패턴 사용 시 기본값 설정
    @Builder.Default
    private List<Proposal> proposals = new ArrayList<>();

    /**
     * JPA가 DB에 insert하기 직전에 호출되는 메서드
     * prepersist로 내부에서 자동 생성되어 @Id필드가 널이 안되도록 보장함
     * // 영속성 저장 시 지정된 아이디가 없으면 생성하여 저장, 있으면 입력값으로 저장
     */
    @PrePersist
    public void prePersist() {
        if (this.campaignId == null) {
            this.campaignId = IdGenerator.campaignId();
        }
    }
}