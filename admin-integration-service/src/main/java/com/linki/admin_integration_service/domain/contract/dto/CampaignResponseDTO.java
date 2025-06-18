package com.linki.admin_integration_service.domain.contract.dto;

import lombok.Data;


import java.time.LocalDate;

@Data
public class CampaignResponseDTO {
    private String campaignId;
    private String advertiserName;
    private String businessNumber;
    private String phone;
    private String campaignTitle;
    private LocalDate registerDate;
    private LocalDate applyDeadline;
    private String campaignLink;
}
