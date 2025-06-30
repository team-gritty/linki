package com.linki.admin_integration_service.domain.account.dto;

import lombok.Data;

@Data
public class AdminPasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
} 