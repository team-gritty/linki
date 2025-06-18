package com.Gritty.Linki.domain.user.advertiser.proposal.dto;

import com.Gritty.Linki.vo.enums.ProposalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 광고주가 받는 제안서 비즈니스 로직 처리용 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalDto {

    private String proposalId; // 제안서 ID
    private String contents; // 제안 내용
    private ProposalStatus status; // 제안 상태
    private LocalDateTime submittedAt; // 인플루언서가 제안서 제안 제출 시점
    private LocalDateTime respondedAt; // 광고주 응답 시점

    // 연관 관계 필드
    private String influencerId; // 제안자 인플루언서 ID
    private String campaignId; // 캠페인 ID
    private String campaignName; // 캠페인명 (조회 시 편의를 위해)
    private String influencerName; // 인플루언서명 (조회 시 편의를 위해)
}