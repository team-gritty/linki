package com.Gritty.Linki.domain.user.influencer.requestDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractSignRequestDTO {
    private String contractId;
    private String redirectUrl;
    private String customValue;
}
