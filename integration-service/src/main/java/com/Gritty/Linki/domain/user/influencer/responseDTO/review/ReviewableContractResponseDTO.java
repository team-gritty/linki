package com.Gritty.Linki.domain.user.influencer.responseDTO.review;

import com.Gritty.Linki.vo.enums.ContractStatus;
import com.Gritty.Linki.vo.enums.SettlementStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ReviewableContractResponseDTO {
    private String contractId;
    private String contractTitle;
    private ContractStatus contractStatus;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private BigDecimal contractAmount;
    private String campaignTitle;
    private SettlementStatus settlementStatus;
}
