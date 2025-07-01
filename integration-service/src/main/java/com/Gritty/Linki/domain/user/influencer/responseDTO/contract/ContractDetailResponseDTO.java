package com.Gritty.Linki.domain.user.influencer.responseDTO.contract;

import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ContractDetailResponseDTO {
    private String contractId;
    private String contractTitle;
    private ContractStatus contractStatus;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private BigDecimal contractAmount;
    private String proposalId;
    private String campaignId;
    private String campaignName;
    private Boolean adDeliveryStatus;
    private String pdfDownloadUrl;     // 가공된 링크
    private LocalDateTime contractCreatedAt;
    private LocalDateTime contractCompletedAt;
    private String contractSpecialTerms;
    private String influencerId;
}
