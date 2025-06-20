package com.linki.admin_integration_service.entity;

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

    @Column(name = "redirect_url", columnDefinition = "LONGTEXT", nullable = false)
    private String redirectUrl; // 변환된 URL

}
