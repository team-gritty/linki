package com.Gritty.Linki.domain.user.influencer.responseDTO.contract;


import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractListResponseDTO {
    private String contractId;
    private String contractTitle;
    private ContractStatus contractStatus;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private BigDecimal contractAmount;
    private String proposalId;
    private String campaignId;
    private String campaignTitle;       // 제안서 → 캠페인 연관된 제목
    private Boolean adDeliveryStatus;   // 광고주 화면에서만 사용

}
