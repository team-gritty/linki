package com.Gritty.Linki.domain.user.influencer.responseDTO.proposal;

import com.Gritty.Linki.vo.enums.ProposalStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalListResponseDTO {
    private String proposalId;
    private String campaignName;
    private String campaignId;
    private LocalDateTime submittedAt;
    private ProposalStatus status;
    private String influencerId;
}
