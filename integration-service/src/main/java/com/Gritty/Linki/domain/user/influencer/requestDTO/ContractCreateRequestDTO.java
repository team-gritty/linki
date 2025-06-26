package com.Gritty.Linki.domain.user.influencer.requestDTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ContractCreateRequestDTO {

    private String redirectUrl; // 유캔사인 api 호출 후 redirecturl
    private String proposalId; // 프론트에서 넘길 기반이 될 제안서 ID

    // 광고주 입력값

    private String advertiserName;
    private String influencerName;
    private String businessNumber;
    private String advertiserAddress;
    private String influencerAddress;


    private String contractSpecialTerms;
    private BigDecimal contractAmount;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private String adDeliveryUrl;


}
