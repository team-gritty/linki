package com.linki.admin_integration_service.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinDTO {
    private String adminLoginId;
    private String adminName;
    private String adminPhone;
    private String adminEmail;
    private String adminAddress;

}
