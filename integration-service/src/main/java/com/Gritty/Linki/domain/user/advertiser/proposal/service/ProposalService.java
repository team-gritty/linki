package com.Gritty.Linki.domain.user.advertiser.proposal.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.proposal.dto.ProposalDto;
import com.Gritty.Linki.domain.user.advertiser.proposal.repository.ProposalRepository;
import com.Gritty.Linki.entity.Proposal;
import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.exception.ErrorCode;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.ProposalStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 광고주 제안서 CRUD 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final AuthenticationUtil authenticationUtil;

    /**
     * 캠페인에 속한 제안서 목록 조회
     * 
     * @param campaignId 캠페인 ID
     * @param user       인증된 사용자 정보
     * @return 제안서 목록
     */
    @Transactional(readOnly = true)
    public List<ProposalDto> getProposalsByCampaign(String campaignId, CustomUserDetails user) {
        // JWT에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        log.info("광고주 {}의 캠페인 {} 제안서 목록 조회 시작", advertiserId, campaignId);

        List<Proposal> proposals = proposalRepository.findByCampaignIdAndAdvertiserId(campaignId, advertiserId);

        log.info("캠페인 {}의 제안서 {}개 조회 완료", campaignId, proposals.size());

        return proposals.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * 제안서 상세 조회
     * 
     * @param proposalId 제안서 ID
     * @param campaignId 캠페인 ID
     * @param user       인증된 사용자 정보
     * @return 제안서 정보
     */
    @Transactional(readOnly = true)
    public ProposalDto getProposalDetail(String proposalId, String campaignId, CustomUserDetails user) {
        // JWT에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        log.info("제안서 {} 상세 조회 시작, 캠페인: {}, 광고주: {}", proposalId, campaignId, advertiserId);

        Proposal proposal = proposalRepository.findByProposalIdAndAdvertiserId(proposalId, advertiserId)
                .orElseThrow(() -> {
                    log.error("제안서를 찾을 수 없거나 접근 권한이 없음: proposalId={}, advertiserId={}", proposalId, advertiserId);
                    return new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "제안서를 찾을 수 없거나 접근 권한이 없습니다");
                });

        // 캠페인 ID 검증
        if (!proposal.getCampaign().getCampaignId().equals(campaignId)) {
            log.error("제안서의 캠페인 ID가 일치하지 않음: expected={}, actual={}", campaignId,
                    proposal.getCampaign().getCampaignId());
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "제안서가 해당 캠페인에 속하지 않습니다");
        }

        log.info("제안서 {} 상세 조회 완료", proposalId);
        return mapToDto(proposal);
    }

    /**
     * 제안서 수락
     * 
     * @param proposalId 제안서 ID
     * @param campaignId 캠페인 ID
     * @param user       인증된 사용자 정보
     * @return 수락된 제안서 정보
     */
    public ProposalDto acceptProposal(String proposalId, String campaignId, CustomUserDetails user) {
        // JWT에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        log.info("제안서 {} 수락 처리 시작, 캠페인: {}, 광고주: {}", proposalId, campaignId, advertiserId);

        Proposal proposal = proposalRepository.findByProposalIdAndAdvertiserId(proposalId, advertiserId)
                .orElseThrow(() -> {
                    log.error("제안서를 찾을 수 없거나 접근 권한이 없음: proposalId={}, advertiserId={}", proposalId, advertiserId);
                    return new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "제안서를 찾을 수 없거나 접근 권한이 없습니다");
                });

        // 캠페인 ID 검증
        if (!proposal.getCampaign().getCampaignId().equals(campaignId)) {
            log.error("제안서의 캠페인 ID가 일치하지 않음: expected={}, actual={}", campaignId,
                    proposal.getCampaign().getCampaignId());
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "제안서가 해당 캠페인에 속하지 않습니다");
        }

        // 이미 응답된 제안서인지 확인 - PENDING 상태가 아니면 이미 응답된 제안서임
        if (proposal.getStatus() != ProposalStatus.PENDING) {
            log.error("이미 응답된 제안서: proposalId={}, currentStatus={}", proposalId, proposal.getStatus());
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 응답된 제안서입니다");
        }

        // 제안서 수락 처리
        proposal.setStatus(ProposalStatus.ACCEPTED);
        proposal.setRespondedAt(LocalDateTime.now());

        Proposal acceptedProposal = proposalRepository.save(proposal);
        log.info("제안서 {} 수락 완료", proposalId);

        return mapToDto(acceptedProposal);
    }

    /**
     * 제안서 거절
     * 
     * @param proposalId 제안서 ID
     * @param campaignId 캠페인 ID
     * @param user       인증된 사용자 정보
     * @return 거절된 제안서 정보
     */
    public ProposalDto rejectProposal(String proposalId, String campaignId, CustomUserDetails user) {
        // JWT에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        log.info("제안서 {} 거절 처리 시작, 캠페인: {}, 광고주: {}", proposalId, campaignId, advertiserId);

        // 거절할 제안서 찾기
        Proposal proposal = proposalRepository.findByProposalIdAndAdvertiserId(proposalId, advertiserId)
                .orElseThrow(() -> {
                    log.error("제안서를 찾을 수 없거나 접근 권한이 없음: proposalId={}, advertiserId={}", proposalId, advertiserId);
                    return new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "제안서를 찾을 수 없거나 접근 권한이 없습니다");
                });

        // 캠페인 ID 검증
        if (!proposal.getCampaign().getCampaignId().equals(campaignId)) {
            log.error("제안서의 캠페인 ID가 일치하지 않음: expected={}, actual={}", campaignId,
                    proposal.getCampaign().getCampaignId());
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "제안서가 해당 캠페인에 속하지 않습니다");
        }

        // 이미 응답된 제안서인지 확인
        if (proposal.getStatus() != ProposalStatus.PENDING) {
            log.error("이미 응답된 제안서: proposalId={}, currentStatus={}", proposalId, proposal.getStatus());
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 응답된 제안서입니다");
        }

        // 제안서 거절 처리
        proposal.setStatus(ProposalStatus.REJECTED);
        proposal.setRespondedAt(LocalDateTime.now());

        Proposal rejectedProposal = proposalRepository.save(proposal);
        log.info("제안서 {} 거절 완료", proposalId);

        return mapToDto(rejectedProposal);
    }

    /**
     * 제안서 수정 (광고주가 수락한 제안서의 내용 수정)
     *
     * @param proposalId 제안서 ID
     * @param user       인증된 사용자 정보
     * @param contents   수정할 내용
     * @return 수정된 제안서 정보
     */
    public ProposalDto updateProposal(String proposalId, CustomUserDetails user, String contents) {
        // JWT에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        log.info("제안서 {} 수정 시작, 광고주: {}", proposalId, advertiserId);

        Proposal proposal = proposalRepository.findByProposalIdAndAdvertiserId(proposalId, advertiserId)
                .orElseThrow(() -> {
                    log.error("제안서를 찾을 수 없거나 접근 권한이 없음: proposalId={}, advertiserId={}", proposalId, advertiserId);
                    return new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "제안서를 찾을 수 없거나 접근 권한이 없습니다");
                });

        // 수락된 제안서만 수정 가능
        if (proposal.getStatus() != ProposalStatus.ACCEPTED) {
            log.error("수락되지 않은 제안서는 수정할 수 없음: proposalId={}, currentStatus={}", proposalId, proposal.getStatus());
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "수락된 제안서만 수정할 수 있습니다");
        }

        // 제안서 내용 수정
        proposal.setContents(contents);

        Proposal updatedProposal = proposalRepository.save(proposal);
        log.info("제안서 {} 수정 완료", proposalId);

        return mapToDto(updatedProposal);
    }

    /**
     * Proposal 엔티티를 DTO로 변환
     * 
     * @param proposal 제안서 엔티티
     * @return 제안서 DTO
     */
    private ProposalDto mapToDto(Proposal proposal) {
        return ProposalDto.builder()
                .proposalId(proposal.getProposalId())
                .contents(proposal.getContents())
                .status(proposal.getStatus())
                .submittedAt(proposal.getSubmittedAt())
                .respondedAt(proposal.getRespondedAt())
                .influencerId(proposal.getInfluencer().getInfluencerId())
                .campaignId(proposal.getCampaign().getCampaignId())
                .campaignName(proposal.getCampaign().getCampaignName())
                .influencerName(proposal.getInfluencer().getInfluencerName())
                .build();
    }
}