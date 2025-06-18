package com.Gritty.Linki.domain.user.advertiser.proposal.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 제안서 거절 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalRejectRequest {

    private String rejectReason; // 거절 사유 (선택사항)
}