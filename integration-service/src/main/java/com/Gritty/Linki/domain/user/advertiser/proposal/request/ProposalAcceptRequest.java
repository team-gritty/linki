package com.Gritty.Linki.domain.user.advertiser.proposal.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 제안서 수락 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalAcceptRequest {

    private String message; // 수락 메시지 (선택사항)
}