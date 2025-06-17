package com.linki.admin_integration_service.domain.operations.dto;

import lombok.Data;

@Data
public class AdminSignUpAgreementResponseDTO {
    private String	adminSignUpId;
    private String	adminName;
    private String	adminEmail;
    private String	adminPhone;
    private String 	adminStatus;
}
