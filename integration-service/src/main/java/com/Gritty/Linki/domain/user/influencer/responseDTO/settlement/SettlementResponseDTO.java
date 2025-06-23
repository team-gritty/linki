package com.Gritty.Linki.domain.user.influencer.responseDTO.settlement;

import com.Gritty.Linki.vo.enums.SettlementStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementResponseDTO {
    private String settlementId;
    private String contractId;
    private BigDecimal settlementAmount;
    private SettlementStatus settlementStatus;
    private LocalDate settlementDate;
    private String campaignName;

}
