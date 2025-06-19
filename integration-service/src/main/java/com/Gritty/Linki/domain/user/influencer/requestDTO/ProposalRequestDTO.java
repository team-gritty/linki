package com.Gritty.Linki.domain.user.influencer.requestDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalRequestDTO {
    private String campaignId;
    private String contents;
}
