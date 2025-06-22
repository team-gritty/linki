package com.linki.admin_integration_service.entity;

import jakarta.persistence.*;
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
}
