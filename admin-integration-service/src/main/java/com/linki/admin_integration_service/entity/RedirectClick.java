package com.linki.admin_integration_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name= "redirect_click")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RedirectClick {
    @Id
    @Column(name = "click_id", length = 25, nullable = false)
    private String clickId; // 클릭 ID

    //로컬 데이트 한다음에 하루 누적 00시
    @Column(name = "click_time", nullable = false)
    private LocalDateTime clickTime; // 클릭 시각

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redirect_id", nullable = false)
    private RedirectLinks redirectLink; // 어떤 리디렉션 링크에 대한 클릭인지

    //클릭수 ++ 레디스 쓰려면 쓰고

}
