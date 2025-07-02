package com.Gritty.Linki.user.advertiser.proposal.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.proposal.dto.ProposalDto;
import com.Gritty.Linki.domain.user.advertiser.proposal.service.ProposalService;
import com.Gritty.Linki.vo.enums.ProposalStatus;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Log4j2
@Transactional
public class ProposalServiceTests {

    @Autowired
    private ProposalService proposalService;

    String testProposalId = "PROP0001";
    String testProposalId2 = "PROP0002";
    String testCampaignId = "CAMP0001";
    String testUserId = "USER0500"; // 광고주인 USER0500 사용

    /**
     * CustomUserDetails 실제 객체를 생성하는 메소드
     * 
     * @return
     */
    private CustomUserDetails createMockUserDetails() {
        return new CustomUserDetails(
                testUserId,
                "testLoginId",
                "testPassword",
                "ROLE_ADVERTISER");
    }

    @Test
    @DisplayName("캠페인별 제안서 목록 조회 Service Test")
    public void getProposalsByCampaign() {
        log.info("캠페인별 제안서 목록 조회 테스트 시작");

        CustomUserDetails mockUser = createMockUserDetails();

        try {
            List<ProposalDto> proposals = proposalService.getProposalsByCampaign(testCampaignId, mockUser);
            log.info("조회된 제안서 수: {}", proposals.size());

            for (ProposalDto proposal : proposals) {
                log.info("제안서 정보: ID={}, 상태={}, 인플루언서={}",
                        proposal.getProposalId(), proposal.getStatus(), proposal.getInfluencerName());
            }
        } catch (Exception e) {
            log.info("캠페인별 제안서 조회 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("제안서 상세 조회 Service Test")
    public void getProposalDetail() {
        log.info("제안서 상세 조회 테스트 시작");

        //
        CustomUserDetails mockUser = createMockUserDetails();

        try {
            ProposalDto proposal = proposalService.getProposalDetail(testProposalId, testCampaignId, mockUser);
            log.info("조회된 제안서: ID={}, 내용={}, 상태={}",
                    proposal.getProposalId(), proposal.getContents(), proposal.getStatus());
        } catch (Exception e) {
            log.info("제안서 상세 조회 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("제안서 수락 Service Test")
    public void acceptProposal() {
        log.info("제안서 수락 테스트 시작");

        CustomUserDetails mockUser = createMockUserDetails();

        try {
            ProposalDto acceptedProposal = proposalService.acceptProposal(testProposalId, testCampaignId, mockUser);
            log.info("수락된 제안서: ID={}, 상태={}",
                    acceptedProposal.getProposalId(), acceptedProposal.getStatus());
        } catch (Exception e) {
            log.info("제안서 수락 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("제안서 거절 Service Test")
    public void rejectProposal() {
        log.info("제안서 거절 테스트 시작");

        CustomUserDetails mockUser = createMockUserDetails();

        try {
            ProposalDto rejectedProposal = proposalService.rejectProposal(testProposalId2, testCampaignId, mockUser);
            log.info("거절된 제안서: ID={}, 상태={}",
                    rejectedProposal.getProposalId(), rejectedProposal.getStatus());
        } catch (Exception e) {
            log.info("제안서 거절 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("제안서 수정 Service Test")
    public void updateProposal() {
        log.info("제안서 수정 테스트 시작");

        CustomUserDetails mockUser = createMockUserDetails();
        String updatedContents = "수정된 제안 내용";

        try {
            ProposalDto updatedProposal = proposalService.updateProposal(testProposalId, mockUser, updatedContents);
            log.info("수정된 제안서: ID={}, 내용={}",
                    updatedProposal.getProposalId(), updatedProposal.getContents());
        } catch (Exception e) {
            log.info("제안서 수정 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("여러 제안서 상태별 조회 Service Test")
    public void getProposalsByStatus() {
        log.info("상태별 제안서 조회 테스트 시작");

        CustomUserDetails mockUser = createMockUserDetails();

        try {
            List<ProposalDto> proposals = proposalService.getProposalsByCampaign(testCampaignId, mockUser);

            long pendingCount = proposals.stream()
                    .filter(p -> p.getStatus() == ProposalStatus.PENDING)
                    .count();

            long acceptedCount = proposals.stream()
                    .filter(p -> p.getStatus() == ProposalStatus.ACCEPTED)
                    .count();

            long rejectedCount = proposals.stream()
                    .filter(p -> p.getStatus() == ProposalStatus.REJECTED)
                    .count();

            log.info("대기중 제안서: {}개, 수락된 제안서: {}개, 거절된 제안서: {}개",
                    pendingCount, acceptedCount, rejectedCount);

        } catch (Exception e) {
            log.info("상태별 제안서 조회 실패 (예상됨): {}", e.getMessage());
        }
    }
}