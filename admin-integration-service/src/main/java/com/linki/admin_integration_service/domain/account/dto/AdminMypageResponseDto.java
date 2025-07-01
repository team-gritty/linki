package com.linki.admin_integration_service.domain.account.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AdminMypageResponseDto {
    private String adminLoginId;
    private String adminName;
    private String adminPhone;
    private String adminEmail;
    private String adminAddress;
    private LocalDate adminEnterDay;
} 