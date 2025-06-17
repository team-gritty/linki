package com.Gritty.Linki.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jwt_refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RefreshToken {

    @Id
    @Column(name = "jwt_id", length = 25)
    private String jwtId;

    @Column(name = "refresh_token", columnDefinition = "LONGTEXT")
    private String refreshToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
