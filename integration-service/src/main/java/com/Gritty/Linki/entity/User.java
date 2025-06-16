package com.Gritty.Linki.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "user_id", length = 25, nullable = false)
    private String userId; // UUID or 소셜 PK

    @Column(name = "user_login_id", length = 20, nullable = false)
    private String userLoginId; // 로그인 ID

    @Column(name = "user_login_pw", length = 255, nullable = false)
    private String userLoginPw; // 비밀번호

    @Column(name = "user_name", length = 50, nullable = false)
    private String userName; // 이름

    @Column(name = "user_phone", length = 15, nullable = false)
    private String userPhone; // 휴대폰 번호

    @Column(name = "user_email", length = 255, nullable = false)
    private String userEmail; // 이메일

    @Column(name = "user_pay_status", nullable = false)
    private Integer userPayStatus; // 결제 상태

    @Column(name = "user_status", nullable = false)
    private Integer userStatus; // 계정 활성/비활성 상태

    @Column(name = "user_enter_day", nullable = false)
    private LocalDate userEnterDay; // 가입일

    @Column(name = "user_role", length = 20, nullable = false)
    private String userRole; // 권한 (예: ROLE_USER, ROLE_ADMIN, 인플루언서, 광고주)
}
