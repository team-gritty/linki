package com.Gritty.Linki.domain.user.influencer.responseDTO;

import com.Gritty.Linki.vo.enums.ProposalStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalDetailResponseDTO {
    private String proposalId;
    private String contents;
    private ProposalStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime respondedAt;
    private String influencerId;
    private String campaignId;
}
