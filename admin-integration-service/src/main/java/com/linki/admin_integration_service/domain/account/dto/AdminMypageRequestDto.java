package com.linki.admin_integration_service.domain.account.dto;

import lombok.Data;

@Data
public class AdminMypageRequestDto {
    private String adminName;
    private String adminPhone;
    private String adminEmail;
    private String adminAddress;
} 