package com.linki.admin_integration_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "admin")
public class Admin {

    @Id
    @Column(name = "admin_id", length = 25)
    private String adminId;

    @Column(name = "admin_login_id", length = 20)
    private String adminLoginId;

    @Column(name = "admin_login_pw", length = 255)
    private String adminLoginPw;

    @Column(name = "admin_name", length = 20)
    private String adminName;

    @Column(name = "admin_phone", length = 15)
    private String adminPhone;

    @Column(name = "admin_email", length = 50)
    private String adminEmail;

    @Column(name = "admin_address", length = 255)
    private String adminAddress;

    @Column(name = "admin_enter_day")
    private LocalDate adminEnterDay;

    @Column(name = "admin_status")
    private String   adminStatus;

}
