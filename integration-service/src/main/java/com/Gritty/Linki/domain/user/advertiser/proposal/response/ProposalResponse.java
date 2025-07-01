package com.Gritty.Linki.domain.user.advertiser.proposal.response;

import com.Gritty.Linki.vo.enums.ProposalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 클라이언트에게 반환되는 제안서 Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalResponse {

    private String proposalId; // 제안서 ID -> 클라이언트한테 줘야하는 정보인가?
    private String contents; // 제안 내용
    private ProposalStatus status; // 제안 상태
    private LocalDateTime submittedAt; // 제안 제출 시점
    private LocalDateTime respondedAt; // 광고주 응답 시점

    // 연관 관계 정보
    private String influencerId; // 제안자 인플루언서 ID
    private String campaignId; // 캠페인 ID
    private String campaignName; // 캠페인명
    private String influencerName; // 인플루언서명
    private String channelName; // 인플루언서 채널명
}