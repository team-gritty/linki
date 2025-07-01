package com.linki.admin_integration_service.domain.account.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RequestJoinDTO {
    private String adminLoginId;
    private String adminLoginPw;
    private String adminName;
    private String adminPhone;
    private String adminEmail;
    private String adminAddress;

}
