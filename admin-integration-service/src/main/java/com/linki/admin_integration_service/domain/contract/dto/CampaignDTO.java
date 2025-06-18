package com.linki.admin_integration_service.domain.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDTO {
    private String campaignId;
    private String advertiserName;
    private String businessNumber;
    private String phone;
    private String campaignTitle;
    private LocalDate registerDate;
    private LocalDate applyDeadline;
    private String campaignLink;
}
