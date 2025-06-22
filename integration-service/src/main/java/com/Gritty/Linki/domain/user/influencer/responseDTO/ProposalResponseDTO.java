package com.Gritty.Linki.domain.user.influencer.responseDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalResponseDTO {
    private String proposalId;
    private String campaignId;
    private String influencerId;
    private String contents;
    private LocalDateTime submittedAt;
}
