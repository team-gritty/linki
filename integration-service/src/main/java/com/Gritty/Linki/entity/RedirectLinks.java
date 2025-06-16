package com.Gritty.Linki.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "redirect_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RedirectLinks {
    @Id
    @Column(name = "redirect_id", length = 25, nullable = false)
    private String redirectId; // 리디렉션 링크 ID

    @Column(name = "origin_url", columnDefinition = "LONGTEXT", nullable = false)
    private String originUrl; // 원본 URL

    @Column(name = "redirected_url", columnDefinition = "LONGTEXT", nullable = false)
    private String redirectedUrl; // 변환된 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertiser_id", nullable = false)
    private Advertiser advertiser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract; // 연결된 계약
}
