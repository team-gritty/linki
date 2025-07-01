package com.Gritty.Linki.domain.user.influencer.responseDTO.proposal;

import com.Gritty.Linki.vo.enums.ContractStatus;
import com.Gritty.Linki.vo.enums.ProposalStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ProposalDetailResponseDTO {
    private String campaignName;
    private String proposalId;
    private String contents;
    private ProposalStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime respondedAt;
    private String influencerId;
    private String campaignId;
    private String contractId;
    private ContractStatus contractStatus;


    public ProposalDetailResponseDTO(
            String campaignName,
            String proposalId,
            String contents,
            ProposalStatus status,
            LocalDateTime submittedAt,
            LocalDateTime respondedAt,
            String influencerId,
            String campaignId,
            String contractId,
            ContractStatus contractStatus
    ) {
        this.campaignName = campaignName;
        this.proposalId = proposalId;
        this.contents = contents;
        this.status = status;
        this.submittedAt = submittedAt;
        this.respondedAt = respondedAt;
        this.influencerId = influencerId;
        this.campaignId = campaignId;
        this.contractId = contractId;
        this.contractStatus = contractStatus;
    }
}


